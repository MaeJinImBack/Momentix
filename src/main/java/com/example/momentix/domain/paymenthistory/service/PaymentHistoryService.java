package com.example.momentix.domain.paymenthistory.service;

import com.example.momentix.domain.paymenthistory.dto.PaymentConfirmRequest;
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
    public PaymentResponse confirmSimple(Long userId, PaymentConfirmRequest req) {
        Long reservationId = req.getReservationId();

        // 1) 예약 소유자 확인
        Reservations r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!r.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        // 2) 결제내역 생성(PENDING)
        PaymentHistory ph = PaymentHistory.create(reservationId, "SELF", "MOCK", null);
        paymentHistoryRepository.save(ph);

        // 3) 티켓 발급
        CreateTicketRequestDto tr = new CreateTicketRequestDto();
        tr.setReservationId(reservationId);
        TicketResponseDto ticket = ticketService.createTicket(tr);

        // 4) 티켓 ← 결제 FK 연결(외래키 주인: 티켓)
        int updated = ticketRepository.linkPayment(ticket.getTicketId(), ph.getPaymentHistoryId());
        if (updated == 0) {
            throw new IllegalStateException("티켓 결제 연결 실패");
        }

        // 5) 결제 성공 표시
        ph.markSuccess();
        return PaymentResponse.of(ph);
    }


    //결제 확정

    //결제 취소/실패
}
