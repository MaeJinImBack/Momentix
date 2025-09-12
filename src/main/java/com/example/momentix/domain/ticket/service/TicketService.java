package com.example.momentix.domain.ticket.service;

import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.momentix.domain.ticket.dto.response.TicketResponseDto;
import com.example.momentix.domain.ticket.entity.Tickets;
import com.example.momentix.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public TicketResponseDto createTicket(CreateTicketRequestDto requestDto) {

        // 1. reservationId로 임시 예매 정보 조회
        Reservations reservation = reservationRepository.findById(requestDto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임시 예매 정보입니다."));

        // 2. 고유한 티켓 번호를 생성합니다.
        String ticketNumber = "MOMENTIX-" + UUID.randomUUID().toString().toUpperCase().substring(0, 13);

        // 3. 임시 예매 정보를 바탕으로 최종 티켓(Tickets) 엔티티를 생성합니다.
        Tickets ticket = new Tickets(
                reservation.getUsers(),
                reservation.getEventSeat().getSeats(),
                reservation.getEventTimes(),
                ticketNumber
        );

        // 4. Ticket 저장 및 Reservation 삭제 (소프트딜리트)
        Tickets savedTicket = ticketRepository.save(ticket);

        reservation.completeTicketIssuance();

        return new TicketResponseDto(savedTicket);
    }
}