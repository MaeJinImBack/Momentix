package com.example.momentix.domain.point.service;

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
        this.paymentHistoryRepository=paymentHistoryRepository;
        this.reservationRepository=reservationRepository;
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

    // 즉시 적립 - 잔액+=amount
    // 같은 멱등키가 이미 처리됐다면, 다시 반영 x 현재 상태만 반환
    // 같은 유저의 포인트 행을 잠그고 point_ledge에 기록
    @Transactional
    public PointBalanceResponse earn(Long userId, String idemKey, long amount, String reason, Long paymentId,
                                     Long reservationId) {
        requirePositive(amount); //0 이하 금액은 잘못된 요청
        if (alreadyDone(userId, idemKey))// 같은 키로 이미 처리된 요청이면
            return getMyPoints(userId);// 그냥 현재 상태만 보내준다 = 멱등

        Points points = lockRow(userId);// 이 유저의 포인트 행을 잠시 잠근다
        points.increaseBalance(amount);// 설제 잔액 증가
        writeLedger(userId, idemKey, PointOperationType.EARN, amount, reason, paymentId, reservationId);
        return snapshot(points);
    }

    // 사용(차감) - 잔액-=amount
    // 잔액이 부족하면 실패
    // point_ledge에는 얼마를 사용했는지가 보이게 음수로 기록
    @Transactional
    public PointBalanceResponse use(Long userId, String idemKey, long amount, String reason, Long paymentId,
                                    Long reservationId) {
        requirePositive(amount);
        if (alreadyDone(userId, idemKey))
            return getMyPoints(userId);

        Points points = lockRow(userId);
        if (points.getPointBalance() < amount) {
            // 여기서는 단순하게 실패만 던진다
            throw new IllegalStateException("포인트 잔액이 부족합니다.");
        }
        points.decreaseBalance(amount);// 실제 잔액 차감
        writeLedger(userId, idemKey, PointOperationType.USE, -amount, reason, paymentId, reservationId);
        return snapshot(points);
    }

    // 적립 예정(+pending) - 확정 아님
    // 바로 쓰이지 않고 나중에 확정 시점에 잔액으로 옮김
    // 결제 직후 (환불가능기간)에는 예정으로 쌓고 환불 불가 시점에 확정으로 전환
    @Transactional
    public PointBalanceResponse earnPending(Long userId, String idemKey, long amount, String reason, Long paymentId,
                                            Long reservationId) {
        requirePositive(amount);
        if (alreadyDone(userId, idemKey))
            return getMyPoints(userId);

        Points points = lockRow(userId);
        points.increasePending(amount); // 예정 금액 증가
        writeLedger(userId, idemKey, PointOperationType.PENDING_EARN, amount, reason, paymentId, reservationId);
        return snapshot(points);
    }

    // 예정 해제(확정 전환): pending -= amount, balance += amount
    // 예정에서 잔액으로 옮기는 동작
    // 예정 금액이 모자라면 실패
    @Transactional
    public PointBalanceResponse releasePending(Long userId, String idemKey, long amount, String reason,
                                               Long paymentId, Long reservationId) {
        requirePositive(amount);
        if (alreadyDone(userId, idemKey))
            return getMyPoints(userId);

        Points points = lockRow(userId);
        if (points.getPointPending() < amount) {
            throw new IllegalStateException("예정 포인트가 부족합니다.");
        }
        points.decreasePending(amount);// 예정에서 빼고 
        points.increaseBalance(amount);// 잔액에 넣는다
        writeLedger(userId, idemKey, PointOperationType.PENDING_RELEASE, amount, reason, paymentId, reservationId);
        return snapshot(points);
    }

    // 결제 성공 시: 할인 후 결제금액의 3%를 "적립 예정"
    // 정책 서비스에서 3% 를 계산(내림)
    // 왜 예정이냐?
    // 환불 가능 기간이 지나서 확정되기 전까지 아직 불확실하기 때문
    @Transactional
    public PointBalanceResponse earnPendingByPaymentAmount(Long userId, String idemKey, Long paymentId,
                                                           Long reservationId, BigDecimal discountedAmount,
                                                           String reason) {
        // 1) 멱등키 체크
        if (alreadyDone(userId, idemKey)) return getMyPoints(userId);

        // 2) 결제 존재/소유/예약일치/상태 검증
        PaymentHistory payment = verifyPaymentOwnershipAndMatch(userId, paymentId, reservationId);
        if (payment.getPaymentStatusType() != PaymentStatusType.SUCCESS) {
            throw new IllegalStateException("결제가 성공(SUCCESS) 상태가 아닙니다.");
        }

        // 3) 같은 결제에 대해 이미 '적립 예정'을 쌓았는지 서버측 멱등 보강
        if (ledgerRepository.existsByUserIdAndPointOperationTypeAndRelatedPaymentId(
                userId, PointOperationType.PENDING_EARN, paymentId)) {
            // 이미 적립 예정이 있다면 현재 상태만 반환(또는 409로 막아도 됨)
            return getMyPoints(userId);
        }

        long earn = policyService.calculateEarnPoints(discountedAmount);
        return earnPending(userId, idemKey, earn, reason, paymentId, reservationId);
    }

    @Transactional
    public PointBalanceResponse releasePendingByPayment(Long userId, String idemKey, Long paymentId, String reason) {
        if (alreadyDone(userId, idemKey)) return getMyPoints(userId);

        // 결제가 내 것인지 확인 (예약 소유자 기준)
        PaymentHistory payment = verifyPaymentOwnership(userId, paymentId);

        long pending = ledgerRepository.sumPendingEarnByPayment(userId, paymentId);
        if (pending <= 0) return getMyPoints(userId);

        return releasePending(userId, idemKey, pending, reason, paymentId, null);
    }

    @Transactional
    public PointBalanceResponse cancelPendingForPayment(Long userId, String idemKey, Long paymentId, String reason) {
        if (alreadyDone(userId, idemKey)) return getMyPoints(userId);

        // 결제가 내 것인지 확인
        PaymentHistory payment = verifyPaymentOwnership(userId, paymentId);

        long pendingAmount = ledgerRepository.sumPendingEarnByPayment(userId, paymentId);
        if (pendingAmount <= 0) return getMyPoints(userId);

        Points p = lockRow(userId);
        if (p.getPointPending() < pendingAmount) pendingAmount = p.getPointPending();
        p.decreasePending(pendingAmount);
        writeLedger(userId, idemKey, PointOperationType.PENDING_CANCEL, -pendingAmount, reason, paymentId, null);
        return snapshot(p);
    }

    @Transactional
    public PointBalanceResponse refundUsedPointsForPayment(Long userId, String idemKey, Long paymentId, String reason) {
        if (alreadyDone(userId, idemKey)) return getMyPoints(userId);

        // 결제가 내 것인지 확인
        PaymentHistory payment = verifyPaymentOwnership(userId, paymentId);

        long used = Math.abs(ledgerRepository.sumAmountByPaymentAndOp(userId, paymentId, PointOperationType.USE));
        if (used <= 0) return getMyPoints(userId);

        Points points = lockRow(userId);
        points.increaseBalance(used);
        writeLedger(userId, idemKey, PointOperationType.REFUND_USE, used, reason, paymentId, null);
        return snapshot(points);
    }

    // 0이하 금액은 잘못된 요청으로 처리
    private void requirePositive(long amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("amount must be positive.");
    }

    // 멱등키 검사
    // usreId, idemKey 조합이 point_ledge에 이미 있으면 이미 처리된 요청.
    // 같은 키로 또 호출돼도 한 번만 반영하려고 쓰는 장치'
    private boolean alreadyDone(Long userId, String idemKey) {
        if (idemKey == null || idemKey.isEmpty())
            throw new IllegalArgumentException("idempotencyKey is required.");
        return ledgerRepository.existsByUserIdAndIdempotencyKey(userId, idemKey);
    }

    //한 유저의 포인트 행을 잠깐 잡아두는 함수
    private Points lockRow(Long userId) {
        Optional<Points> opt = pointsRepository.findForUpdate(userId);
        return opt.orElseGet(() -> pointsRepository.save(new Points(userId, 0L, 0L)));
    }

    // 누가 무슨 작업, 얼마, 왜, 어떤 결제/예약과 관련을 남긴다
    private void writeLedger(Long userId, String key, PointOperationType type, long amount, String reason,
                             Long paymentId, Long reservationId) {
        try {
            ledgerRepository.save(new PointLedger(userId, key, type, amount, reason, paymentId, reservationId));
        } catch (DataIntegrityViolationException e) {
            // (user_id, idempotency_key) UNIQUE 충돌 → 이미 처리된 요청(멱등)
            // 조용히 무시하고 현재 상태 반환 흐름 유지
        }
    }

    private PointBalanceResponse snapshot(Points p) {
        return new PointBalanceResponse(p.getUserId(), p.getPointBalance(), p.getPointPending());
    }

    // 결제가 존재하고, 그 결제의 예약이 로그인 유저의 것인지 확인
    private PaymentHistory verifyPaymentOwnership(Long userId, Long paymentId) {
        PaymentHistory payment = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        Reservations r = reservationRepository.findById(payment.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!r.getUsers().getUserId().equals(userId)) {
            throw new SecurityException("본인 결제가 아닙니다.");
        }
        return payment;
    }

    // 결제 소유 + 요청한 reservationId와 결제의 reservationId가 일치하는지까지 확인
    private PaymentHistory verifyPaymentOwnershipAndMatch(Long userId, Long paymentId, Long reservationId) {
        PaymentHistory payment = verifyPaymentOwnership(userId, paymentId);
        if (reservationId != null && !payment.getReservationId().equals(reservationId)) {
            throw new IllegalArgumentException("예약 정보가 결제와 일치하지 않습니다.");
        }
        return payment;
    }
}