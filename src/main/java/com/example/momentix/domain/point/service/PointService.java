package com.example.momentix.domain.point.service;

import com.example.momentix.domain.common.exception.paymenthistory.PaymentHistoryErrorException;
import static com.example.momentix.domain.common.exception.paymenthistory.PaymentHistoryCode.*;
import com.example.momentix.domain.common.exception.point.PointErrorException;
import com.example.momentix.domain.common.exception.reservation.ReservationErrorException;
import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.paymenthistory.entity.PaymentStatusType;
import com.example.momentix.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.example.momentix.domain.point.dto.PointBalanceResponse;
import com.example.momentix.domain.point.entity.PointLedger;
import com.example.momentix.domain.point.entity.PointOperationType;
import com.example.momentix.domain.point.entity.Points;
import com.example.momentix.domain.point.repository.PointLedgerRepository;
import com.example.momentix.domain.point.repository.PointsRepository;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import static com.example.momentix.domain.common.exception.reservation.ReservationErrorCode.*;
import static com.example.momentix.domain.common.exception.point.PointCode.*;
import java.math.BigDecimal;
import java.util.Optional;

//비관적락+멱등키
@Service
public class PointService {
    private final PointsRepository pointsRepository;
    private final PointLedgerRepository ledgerRepository;
    private final PointsPolicyService policyService;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ReservationRepository reservationRepository;

    public PointService(PointsRepository pointsRepository, PointLedgerRepository ledgerRepository,
                        PointsPolicyService policyService, PaymentHistoryRepository paymentHistoryRepository, ReservationRepository reservationRepository) {
        this.pointsRepository = pointsRepository;
        this.ledgerRepository = ledgerRepository;
        this.policyService = policyService;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.reservationRepository = reservationRepository;
    }

//    같은 요청이 와도 한 번만 반영되게 함
//    point_ledge에 user_id, idempotency_key로 이미 처리 됐는지 확인
//    같은 키로 다시 오면 현재 잔액만 돌려주고 더 이상 반영하지 않음
//    비관적락: 한 유저의 포인트 행을 잠깐 줄 세우기해서 안전하게 처리
//    SELECT...FOR UPDATE (Spring Data:@Lock(PESSIMISTIC_WRITE)사용)
//    같은 유저가 동시에 갱신하더라도 한 번에 한 명만 해당 행을 바꿈(데이터 꼬임 방지)


    // 조회 - 행이 없으면 0원(pointBalance)/0원(pointPending)으로 새로 만들어 준다(최초 이용자 대비)
    public PointBalanceResponse getMyPoints(Long userId) {
        Points points = pointsRepository.findByUserId(userId).orElseGet(() -> pointsRepository.save(new Points(userId
                , 0L, 0L)));
        return snapshot(points);
    }

    // 사용(차감) - 잔액-=amount
    // 잔액이 부족하면 실패
    // point_ledge에는 얼마를 사용했는지가 보이게 음수로 기록
    @Transactional
    public PointBalanceResponse use(Long userId, String idempotencyKey, long amount, String reason,
                                    Long paymentId, Long reservationId) {
        requirePositive(amount);
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        Points points = lockRow(userId);
        if (points.getPointBalance() < amount) {
            throw new PointErrorException(INSUFFICIENT_POINTS);
        }
        points.decreaseBalance(amount);
        writeLedger(userId, idempotencyKey, PointOperationType.USE, -amount, reason, paymentId, reservationId);
        return snapshot(points);
    }

    // 적립 예정(+pending) - 확정 아님
    // 바로 쓰이지 않고 나중에 확정 시점에 잔액으로 옮김
    // 결제 직후 (환불가능기간)에는 예정으로 쌓고 환불 불가 시점에 확정으로 전환
    @Transactional
    public PointBalanceResponse earnPending(Long userId, String idempotencyKey, long amount, String reason,
                                            Long paymentId, Long reservationId) {
        requirePositive(amount);
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        Points points = lockRow(userId);
        points.increasePending(amount);
        writeLedger(userId, idempotencyKey, PointOperationType.PENDING_EARN, amount, reason, paymentId, reservationId);
        return snapshot(points);
    }


    // 예정 해제(확정 전환): pending -= amount, balance += amount
    // 예정에서 잔액으로 옮기는 동작
    // 예정 금액이 모자라면 실패
    @Transactional
    public PointBalanceResponse releasePending(Long userId, String idempotencyKey, long amount, String reason,
                                               Long paymentId, Long reservationId) {
        requirePositive(amount);
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        Points points = lockRow(userId);
        if (points.getPointPending() < amount) {
            throw new PointErrorException(INSUFFICIENT_PENDING_POINTS);
        }
        points.decreasePending(amount);
        points.increaseBalance(amount);
        writeLedger(userId, idempotencyKey, PointOperationType.PENDING_RELEASE, amount, reason, paymentId, reservationId);
        return snapshot(points);
    }

    // 결제 성공 시: 할인 후 결제금액의 3%를 "적립 예정"
    // 정책 서비스에서 3% 를 계산(내림)
    // 왜 예정이냐?
    // 환불 가능 기간이 지나서 확정되기 전까지 아직 불확실하기 때문
    @Transactional
    public PointBalanceResponse earnPendingByPaymentAmount(Long userId, String idempotencyKey, Long paymentId,
                                                           Long reservationId, BigDecimal discountedAmount, String reason, PaymentStatusType paymentStatusType) {
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        PaymentHistory payment = verifyPaymentOwnershipAndMatch(userId, paymentId, reservationId);
        if (paymentStatusType != PaymentStatusType.SUCCESS) {
            throw new PaymentHistoryErrorException(PAYMENT_NOT_FOUND);
        }

        if (ledgerRepository.existsByUserIdAndPointOperationTypeAndRelatedPaymentId(
                userId, PointOperationType.PENDING_EARN, paymentId)) {
            return getMyPoints(userId);
        }

        long earn = policyService.calculateEarnPoints(discountedAmount);
        return earnPending(userId, idempotencyKey, earn, reason, paymentId, reservationId);
    }

    // 환불 불가 시점: 해당 결제의 적립 예정 전부 확정
    @Transactional
    public PointBalanceResponse releasePendingByPayment(Long userId, String idempotencyKey, Long paymentId, String reason) {
        verifyPaymentOwnership(userId, paymentId);
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        if (ledgerRepository.existsByUserIdAndPointOperationTypeAndRelatedPaymentId(
                userId, PointOperationType.PENDING_RELEASE, paymentId)) {
            return getMyPoints(userId);
        }

        long pending = ledgerRepository.sumPendingEarnByPayment(userId, paymentId);
        if (pending <= 0) return getMyPoints(userId);

        return releasePending(userId, idempotencyKey, pending, reason, paymentId, null);
    }

    // 결제 취소(환불 불가 이전): 해당 결제의 적립 예정 취소
    @Transactional
    public PointBalanceResponse cancelPendingForPayment(Long userId, String idempotencyKey, Long paymentId, String reason) {
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        verifyPaymentOwnership(userId, paymentId);

        long pendingAmount = ledgerRepository.sumPendingEarnByPayment(userId, paymentId);
        if (pendingAmount <= 0) return getMyPoints(userId);

        Points points = lockRow(userId);
        if (points.getPointPending() < pendingAmount) pendingAmount = points.getPointPending();
        points.decreasePending(pendingAmount);
        writeLedger(userId, idempotencyKey, PointOperationType.PENDING_CANCEL, -pendingAmount, reason, paymentId, null);
        return snapshot(points);
    }

    @Transactional
    public PointBalanceResponse refundUsedPointsForPayment(Long userId, String idempotencyKey, Long paymentId, String reason) {
        if (alreadyDone(userId, idempotencyKey)) return getMyPoints(userId);

        verifyPaymentOwnership(userId, paymentId);

        long used = Math.abs(ledgerRepository.sumAmountByPaymentAndOp(userId, paymentId, PointOperationType.USE));
        if (used <= 0) return getMyPoints(userId);

        Points points = lockRow(userId);
        points.increaseBalance(used);
        writeLedger(userId, idempotencyKey, PointOperationType.REFUND_USE, used, reason, paymentId, null);
        return snapshot(points);
    }

    // 0이하 금액은 잘못된 요청으로 처리
    private void requirePositive(long amount) {
        if (amount <= 0) throw new PointErrorException(INVALID_POINT_AMOUNT);
    }

    private boolean alreadyDone(Long userId, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isEmpty())
            throw new PaymentHistoryErrorException(DUPLEICATED_REQUEST);
        return ledgerRepository.existsByUserIdAndIdempotencyKey(userId, idempotencyKey);
    }

    private Points lockRow(Long userId) {
        Optional<Points> opt = pointsRepository.findForUpdate(userId);
        return opt.orElseGet(() -> pointsRepository.save(new Points(userId, 0L, 0L)));
    }

    private void writeLedger(Long userId, String idempotencyKey, PointOperationType type, long amount, String reason,
                             Long paymentId, Long reservationId) {
        try {
            ledgerRepository.save(new PointLedger(userId, idempotencyKey, type, amount, reason, paymentId, reservationId));
        } catch (DataIntegrityViolationException ignored) {
            // (user_id, idempotency_key) UNIQUE 충돌 → 이미 처리된 요청(멱등)
        }
    }

    private PointBalanceResponse snapshot(Points points) {
        return new PointBalanceResponse(points.getUserId(), points.getPointBalance(), points.getPointPending());
    }

    private PaymentHistory verifyPaymentOwnership(Long userId, Long paymentId) {
        PaymentHistory payment = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentHistoryErrorException(PAYMENT_NO_SUCCESS));

        Reservations reservation = reservationRepository.findById(payment.getReservationId())
                .orElseThrow(() -> new ReservationErrorException(NO_RESERVATION));

        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new PaymentHistoryErrorException(PAYMENT_FORBIDDEN);
        }
        return payment;
    }

    private PaymentHistory verifyPaymentOwnershipAndMatch(Long userId, Long paymentId, Long reservationId) {
        PaymentHistory payment = verifyPaymentOwnership(userId, paymentId);
        if (reservationId != null && !payment.getReservationId().equals(reservationId)) {
            throw new PaymentHistoryErrorException(PAYMENT_RESERVATION_MISMATCH);
        }
        return payment;
    }
}