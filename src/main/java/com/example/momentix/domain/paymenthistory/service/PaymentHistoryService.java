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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
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
        this.reservationRepository = reservationRepository;
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
    }

    // 결제 생성(PENDING) - 상태만 관리하는 결제 + FK 주인(티켓)
    //idempotencyKey가 같으면 항상 같은 결과를 반환(중복 생성 방지)
    //(reservationId, status) UNIQUE로 동일 예약의 PENDING 2개 생성 불가
    @Transactional
    public PaymentResponse create(Long userId, PaymentCreateRequest req) {
        if (req.getIdempotencyKey() == null || req.getIdempotencyKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idempotencyKey가 필요합니다.");
        }

        // 1) 같은 예약 + 같은 멱등키면 기존 결과 그대로 반환
        Optional<PaymentHistory> existing =
                paymentHistoryRepository.findByReservationIdAndIdempotencyKey(
                        req.getReservationId(), req.getIdempotencyKey());

        if (existing.isPresent()) {
            Reservations r = reservationRepository.findById(existing.get().getReservationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."));
            if (!r.getUsers().getUserId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 예약이 아닙니다.");
            }
            return PaymentResponse.of(existing.get());
        }

        // 2) 소유자 검증 (+ 필요시 예약 행 락)
        Reservations r = reservationRepository.findByIdForUpdate(req.getReservationId())
                .orElseGet(() -> reservationRepository.findById(req.getReservationId()).orElse(null));
        if (r == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.");
        if (!r.getUsers().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 예약이 아닙니다.");
        }

        // 3) 생성 시도
        PaymentHistory ph = PaymentHistory.create(
                req.getReservationId(), req.getPayer(), req.getPaymentMethod(),
                req.getPaymentPrice(), req.getIdempotencyKey());

        try {
            paymentHistoryRepository.saveAndFlush(ph);
        } catch (DataIntegrityViolationException e) {
            // 3-1) 더블클릭/재시도 등으로 이미 같은 (reservationId, idempotencyKey)가 들어간 경우 → 기존 반환
            Optional<PaymentHistory> dup =
                    paymentHistoryRepository.findByReservationIdAndIdempotencyKey(
                            req.getReservationId(), req.getIdempotencyKey());
            if (dup.isPresent())
                return PaymentResponse.of(dup.get());

            // 3-2) 동일 예약에 PENDING이 이미 있는 경우 (상태 유니크 충돌)
            if (paymentHistoryRepository.existsPendingByReservation(req.getReservationId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 이 예약에 결제 대기 건이 존재합니다.");
            }

            // 기타 제약 위반
            throw new ResponseStatusException(HttpStatus.CONFLICT, "결제 생성 중 제약조건 위반");
        }

        return PaymentResponse.of(ph);
    }


    // 결제 확정 (비관적 락 + 멱등)
    @Transactional
    public PaymentResponse confirm(Long userId, Long paymentId, PaymentConfirmRequest req) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "결제 내역이 없습니다."));
        if (!paymentHistory.getReservationId().equals(req.getReservationId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "예약 정보가 결제와 일치하지 않습니다.");
        }

        //  예약 행 선점
        Reservations reservations = reservationRepository.findByIdForUpdate(req.getReservationId())
                .orElseGet(() -> reservationRepository.findById(req.getReservationId()).orElse(null));
        if (reservations == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.");
        if (!reservations.getUsers().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 예약이 아닙니다.");
        }

        // 이미 링크된 티켓이면 멱등 성공
        Optional<Long> linkedTicketIdOpt = ticketRepository.findIdByPaymentId(paymentId);
        if (linkedTicketIdOpt.isPresent()) {
            if (paymentHistory.getPaymentStatusType() == PaymentStatusType.PENDING)
                paymentHistory.markSuccess();
            return PaymentResponse.of(paymentHistory);
        }

        // 같은 예약 티켓 중복 방지
        if (ticketRepository.existsTicketByReservationId(req.getReservationId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 이 예약으로 발급된 티켓이 있습니다.");
        }

        // 티켓 발급 & 링크
        CreateTicketRequestDto ticketReq = new CreateTicketRequestDto();
        ticketReq.setReservationId(req.getReservationId());
        TicketResponseDto ticket = ticketService.createTicket(ticketReq);

        int updated = ticketRepository.linkPayment(ticket.getTicketId(), paymentHistory);
        if (updated == 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "티켓 결제 연결 실패");

        paymentHistory.markSuccess();
        return PaymentResponse.of(paymentHistory);
    }

    // 결제 취소 (비관적 락)
    @Transactional
    public PaymentResponse cancel(Long userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "결제 내역이 없습니다."));

        Reservations reservations = reservationRepository.findByIdForUpdate(paymentHistory.getReservationId())
                .orElseGet(() -> reservationRepository.findById(paymentHistory.getReservationId()).orElse(null));
        if (reservations == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다.");
        if (!reservations.getUsers().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 예약이 아닙니다.");
        }

        if (paymentHistory.getPaymentStatusType() == PaymentStatusType.CANCEL)
            return PaymentResponse.of(paymentHistory);

        if (paymentHistory.getPaymentStatusType() == PaymentStatusType.PENDING) {
            paymentHistory.markCancel();
            return PaymentResponse.of(paymentHistory);
        }

        if (paymentHistory.getPaymentStatusType() == PaymentStatusType.SUCCESS) {
            Optional<Long> ticketIdOpt = ticketRepository.findIdByPaymentId(paymentId);
            if (ticketIdOpt.isPresent()) {
                Long ticketId = ticketIdOpt.get();
                Tickets ticket = ticketRepository.findById(ticketId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "티켓을 찾을 수 없습니다."));
                ticket.softDelete();

                int unlinked = ticketRepository.unlinkPayment(ticketId, paymentId);
                if (unlinked == 0)
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "티켓 결제 연결 해제 실패");
            }
            paymentHistory.markCancel();
            return PaymentResponse.of(paymentHistory);
        }

        paymentHistory.markCancel(); // FAILED → CANCEL
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