package com.example.momentix.domain.paymenthistory.service;

import com.example.momentix.domain.paymenthistory.dto.PaymentConfirmRequest;
import com.example.momentix.domain.paymenthistory.dto.PaymentCreateRequest;
import com.example.momentix.domain.paymenthistory.dto.PaymentResponse;
import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.paymenthistory.entity.PaymentStatusType;
import com.example.momentix.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.example.momentix.domain.point.service.PointService;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.momentix.domain.ticket.dto.response.TicketResponseDto;
import com.example.momentix.domain.ticket.entity.Tickets;
import com.example.momentix.domain.ticket.repository.TicketRepository;
import com.example.momentix.domain.ticket.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final PointService pointService;

    public PaymentHistoryService(
            PaymentHistoryRepository paymentHistoryRepository,
            ReservationRepository reservationRepository,
            TicketRepository ticketRepository,
            TicketService ticketService,
            PointService pointService
    ) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.reservationRepository = reservationRepository;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.pointService = pointService;
    }

    // 결제 생성(PENDING) - 상태만 관리하는 결제 + FK 주인(티켓)
    //idempotencyKey가 같으면 항상 같은 결과를 반환(중복 생성 방지)
    //(reservationId, status) UNIQUE로 동일 예약의 PENDING 2개 생성 불가
    @Transactional
    public PaymentResponse create(Long userId, PaymentCreateRequest paymentCreateRequest) {
        Reservations reservation = reservationRepository.findById(paymentCreateRequest.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        if (paymentHistoryRepository.existsPendingByReservation(paymentCreateRequest.getReservationId())) {
            throw new IllegalStateException("이미 대기 중(PENDING)인 결제가 있습니다.");
        }

        PaymentHistory paymentHistory = PaymentHistory.create(
                paymentCreateRequest.getReservationId(),
                paymentCreateRequest.getPayer() == null ? "SELF" : paymentCreateRequest.getPayer(),
                paymentCreateRequest.getPaymentMethod() == null ? "MOCK" : paymentCreateRequest.getPaymentMethod(),
                paymentCreateRequest.getPaymentPrice(),
                paymentCreateRequest.getIdempotencyKey()
        );
        paymentHistoryRepository.save(paymentHistory);
        return PaymentResponse.of(paymentHistory);
    }

    // 결제 확정 (비관적 락 + 멱등)
    @Transactional
    public PaymentResponse confirm(Long userId, Long paymentId, PaymentConfirmRequest paymentConfirmRequest) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        if (!paymentHistory.getReservationId().equals(paymentConfirmRequest.getReservationId())) {
            throw new IllegalArgumentException("예약 정보가 결제와 일치하지 않습니다.");
        }

        Reservations reservation = reservationRepository.findById(paymentConfirmRequest.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        // 1) 포인트 사용(선택) - 멱등키 고정 생성
        if (paymentConfirmRequest.getPointsToUse() > 0) {
            String useIdemKey = "PAY-" + paymentHistory.getPaymentHistoryId() + "-POINT-USE";
            pointService.use(
                    userId,
                    useIdemKey,
                    paymentConfirmRequest.getPointsToUse(),
                    "결제 시 포인트 사용",
                    paymentHistory.getPaymentHistoryId(),
                    paymentHistory.getReservationId()
            );
        }

        // 2) 이미 티켓 링크가 있으면 멱등 처리
        Optional<Long> linkedTicketIdOpt = ticketRepository.findIdByPaymentId(paymentId);
        if (linkedTicketIdOpt.isPresent()) {
            if (paymentHistory.getPaymentStatusType() == PaymentStatusType.PENDING) {
                paymentHistory.markSuccess();
                triggerPointPending(userId, paymentHistory); // 멱등키 덕분에 중복 호출 시 무해
            }
            return PaymentResponse.of(paymentHistory);
        }

        // 3) 같은 예약으로 이미 발급된 티켓이 있으면 중복 방지
        boolean ticketExists = ticketRepository.existsTicketByReservationId(paymentConfirmRequest.getReservationId());
        if (ticketExists) {
            throw new IllegalStateException("이미 이 예약으로 발급된 티켓이 있습니다.");
        }

        // 4) 티켓 발급
        CreateTicketRequestDto createTicketRequestDto = new CreateTicketRequestDto();
        createTicketRequestDto.setReservationId(paymentConfirmRequest.getReservationId());
        TicketResponseDto ticket = ticketService.createTicket(createTicketRequestDto);

        // 5) 티켓에 결제ID 링크 (FK 주인: 티켓)
        int updated = ticketRepository.linkPayment(ticket.getTicketId(), paymentHistory);
        if (updated == 0) {
            throw new IllegalStateException("티켓 결제 연결 실패");
        }

        // 6) 결제 성공 마킹
        paymentHistory.markSuccess();

        // 7) 적립 예정(3%) 트리거 (멱등)
        triggerPointPending(userId, paymentHistory);

        return PaymentResponse.of(paymentHistory);
    }

    // 결제 취소 (비관적 락)
    @Transactional
    public PaymentResponse cancel(Long userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        Reservations reservation = reservationRepository.findById(paymentHistory.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        if (paymentHistory.getPaymentStatusType() == PaymentStatusType.CANCEL) {
            return PaymentResponse.of(paymentHistory);
        }

        if (paymentHistory.getPaymentStatusType() == PaymentStatusType.PENDING) {
            paymentHistory.markCancel();
            return PaymentResponse.of(paymentHistory);
        }

        if (paymentHistory.getPaymentStatusType() == PaymentStatusType.SUCCESS) {
            Optional<Long> ticketIdOpt = ticketRepository.findIdByPaymentId(paymentId);
            if (ticketIdOpt.isPresent()) {
                Long ticketId = ticketIdOpt.get();
                Tickets ticket = ticketRepository.findById(ticketId)
                        .orElseThrow(() -> new IllegalStateException("티켓을 찾을 수 없습니다."));
                ticket.softDelete();

                int unlinked = ticketRepository.unlinkPayment(ticketId, paymentId);
                if (unlinked == 0) {
                    throw new IllegalStateException("티켓 결제 연결 해제 실패");
                }
            }

            paymentHistory.markCancel();

            // (A) 이 결제로 쌓인 적립 예정 포인트는 취소
            String cancelPendingIdemKey = "PAY-" + paymentHistory.getPaymentHistoryId() + "-PEND-CANCEL";
            pointService.cancelPendingForPayment(
                    userId,
                    cancelPendingIdemKey,
                    paymentHistory.getPaymentHistoryId(),
                    "결제 취소로 적립 예정 취소"
            );

            // (B) 이 결제로 사용했던 포인트가 있으면 환급
            String refundUseIdemKey = "PAY-" + paymentHistory.getPaymentHistoryId() + "-REFUND-USE";
            pointService.refundUsedPointsForPayment(
                    userId,
                    refundUseIdemKey,
                    paymentHistory.getPaymentHistoryId(),
                    "결제 취소로 포인트 사용 환급"
            );

            return PaymentResponse.of(paymentHistory);
        }

        // FAILED → CANCEL 로 정리
        paymentHistory.markCancel();
        return PaymentResponse.of(paymentHistory);
    }

    //단건조회
    @Transactional
    public PaymentResponse getOne(Long userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "결제 내역이 없습니다."));

        Reservations reservation = reservationRepository.findById(paymentHistory.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."));

        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 결제가 아닙니다.");
        }
        return PaymentResponse.of(paymentHistory);
    }

    private void triggerPointPending(Long userId, PaymentHistory paymentHistory) {
        BigDecimal discountedAmount = paymentHistory.getPaymentPrice(); // 필요 시 실제 할인 반영
        String idemKey = "PAY-" + paymentHistory.getPaymentHistoryId() + "-PEND-EARN";
        pointService.earnPendingByPaymentAmount(
                userId,
                idemKey,
                paymentHistory.getPaymentHistoryId(),
                paymentHistory.getReservationId(),
                discountedAmount,
                "결제 적립 예정(3%)"
        );
    }
}