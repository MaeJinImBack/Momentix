package com.example.momentix.domain.ticket.service;

import com.example.momentix.domain.reservation.entity.Reservations;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.momentix.domain.ticket.dto.response.TicketResponseDto;
import com.example.momentix.domain.ticket.entity.Tickets;
import com.example.momentix.domain.ticket.repository.TicketRepository;
import com.example.momentix.domain.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

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

    // 내 티켓 내역 전체 조회
    public Page<TicketResponseDto> getMyTickets(Users user, Pageable pageable) {

        Page<Tickets> ticketPage = ticketRepository.findByUsersAndIsDeletedFalse(user, pageable);

        return ticketPage.map(TicketResponseDto::new);
    }

    // 내 티켓 내역 단건 조회
    public TicketResponseDto getTicket(Long ticketId, Users user) {

        Tickets ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예매 내역을 찾을 수 없습니다."));

        // 찾은 티켓의 주인과 현재 로그인한 유저가 같은지 확인
        if (!ticket.getUsers().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("조회 권한이 없습니다.");
        }

        return new TicketResponseDto(ticket);
    }
}