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
import com.example.momentix.domain.ticket.entity.Tickets;
import com.example.momentix.domain.ticket.repository.TicketRepository;
import com.example.momentix.domain.ticket.service.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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

    // 결제 생성(PENDING) - 상태만 관리하는 결제 + FK 주인(티켓)
    @Transactional
    public PaymentResponse create(Long userId, PaymentCreateRequest paymentCreateRequest) {
        Reservations r = reservationRepository.findById(paymentCreateRequest.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!r.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        if (paymentHistoryRepository.existsPendingByReservation(paymentCreateRequest.getReservationId())) {
            throw new IllegalStateException("이미 대기 중(PENDING)인 결제가 있습니다.");
        }

        PaymentHistory paymentHistory = PaymentHistory.create(
                paymentCreateRequest.getReservationId(),
                paymentCreateRequest.getPayer() == null ? "SELF" : paymentCreateRequest.getPayer(),
                paymentCreateRequest.getPaymentMethod() == null ? "MOCK" : paymentCreateRequest.getPaymentMethod(),
                paymentCreateRequest.getPaymentPrice()
        );
        paymentHistoryRepository.save(paymentHistory);
        return PaymentResponse.of(paymentHistory);
    }

    // 결제 확정 (멱등)
    @Transactional
    public PaymentResponse confirm(Long userId, Long paymentId, PaymentConfirmRequest paymentConfirmRequest) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        if (!paymentHistory.getReservationId().equals(paymentConfirmRequest.getReservationId())) {
            throw new IllegalArgumentException("예약 정보가 결제와 일치하지 않습니다.");
        }

        Reservations r = reservationRepository.findById(paymentConfirmRequest.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!r.getUsers().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 예약이 아닙니다.");
        }

        // 이미 이 paymentId로 링크된 티켓이 있으면 성공 간주
        Optional<Long> linkedTicketIdOpt = ticketRepository.findIdByPaymentId(paymentId);
        if (linkedTicketIdOpt.isPresent()) {
            if (paymentHistory.getPaymentStatusType() == PaymentStatusType.PENDING) {
                paymentHistory.markSuccess();
            }
            return PaymentResponse.of(paymentHistory);
        }

        // 같은 예약으로 이미 발급된 티켓이 있으면 중복 발급 방지
        boolean ticketExists = ticketRepository.existsTicketByReservationId(paymentConfirmRequest.getReservationId());
        if (ticketExists) {
            throw new IllegalStateException("이미 이 예약으로 발급된 티켓이 있습니다.");
        }

        // 티켓 발급
        CreateTicketRequestDto ticketReq = new CreateTicketRequestDto();
        ticketReq.setReservationId(paymentConfirmRequest.getReservationId());
        TicketResponseDto ticket = ticketService.createTicket(ticketReq);

        // 티켓에 결제ID 링크 (FK 주인: 티켓)
        int updated = ticketRepository.linkPayment(ticket.getTicketId(), paymentHistory);
        if (updated == 0) {
            throw new IllegalStateException("티켓 결제 연결 실패");
        }

        paymentHistory.markSuccess();
        return PaymentResponse.of(paymentHistory);
    }


    // 결제 취소
    @Transactional
    public PaymentResponse cancel(Long userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 없습니다."));

        // 본인 예약 여부 체크
        Reservations reservations = reservationRepository.findById(paymentHistory.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        if (!reservations.getUsers().getUserId().equals(userId)) {
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
            return PaymentResponse.of(paymentHistory);
        }

        // FAILED
        paymentHistory.markCancel();
        return PaymentResponse.of(paymentHistory);
    }

    //단건조회
    @Transactional
    public PaymentResponse getOne(Long userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "결제 내역이 없습니다."));

        // 결제 -> 예약 -> 사용자 소유 확인
        Reservations reservations = reservationRepository.findById(paymentHistory.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."));

        if (!reservations.getUsers().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 결제가 아닙니다.");
        }

        return PaymentResponse.of(paymentHistory);
    }
}