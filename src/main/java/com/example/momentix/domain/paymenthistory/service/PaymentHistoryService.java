package com.example.momentix.domain.paymenthistory.service;

import com.example.momentix.domain.paymenthistory.dto.PaymentCreateRequest;
import com.example.momentix.domain.paymenthistory.dto.PaymentResponse;
import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.paymenthistory.entity.PaymentStatusType;
import com.example.momentix.domain.paymenthistory.repository.PaymentHistoryRepository;
import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.momentix.domain.ticket.dto.response.TicketResponseDto;
import com.example.momentix.domain.ticket.repository.TicketRepository;
import com.example.momentix.domain.ticket.service.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    public PaymentHistoryService(
            PaymentHistoryRepository paymentHistoryRepository,
            ReservationRepository reservationRepository,
            TicketRepository ticketRepository,
            TicketService ticketService
    ) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.reservationRepository=reservationRepository;
        this.ticketService=ticketService;
        this.ticketRepository=ticketRepository;
    }

    // 결제 생성(PENDING) -상태만 관리하는 결제 + FK 주인(티켓)
    @Transactional
    public PaymentResponse create(Long userId, PaymentCreateRequest req) {
        var reservation = reservationRepository.findById(req.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        // 같은 예약에 이미 PENDING 결제가 있으면 금지
        if (paymentHistoryRepository.existsPendingByReservation(req.getReservationId())) {
            throw new IllegalStateException("이미 대기 중(PENDING)인 결제가 있습니다.");
        }

        var ph = PaymentHistory.create(
                req.getReservationId(),
                req.getPayer() == null ? "SELF" : req.getPayer(),
                req.getPaymentMethod() == null ? "MOCK" : req.getPaymentMethod(),
                req.getPaymentPrice()
        );
        paymentHistoryRepository.save(ph);
        return PaymentResponse.of(ph); // PENDING
    }


    //결제 확정
    @Transactional
    public PaymentResponse confirm(Long userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        if (paymentHistory.getPaymentStatusType() != PaymentStatusType.PENDING) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }

        Long reservationId = paymentHistory.getReservationId();

        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservation.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        // 이미 다른 티켓에 연결되어 있나 체크 (안전장치)
        if (ticketRepository.existsPaymentLinked(paymentId)) {
            throw new IllegalStateException("이미 티켓에 연결된 결제입니다.");
        }

        // 티켓 발급
        CreateTicketRequestDto ticketReq = new CreateTicketRequestDto();
        ticketReq.setReservationId(reservationId);
        TicketResponseDto ticket = ticketService.createTicket(ticketReq);

        // FK 주인은 티켓 → 결제ID를 티켓에 세팅
        int updated = ticketRepository.linkPayment(ticket.getTicketId(), paymentHistory.getPaymentHistoryId());
        if (updated == 0) {
            throw new IllegalStateException("티켓 결제 연결 실패");
        }

        // 상태 SUCCESS
        paymentHistory.markSuccess();
        return PaymentResponse.of(paymentHistory);
    }


    //결제 취소/실패
}
