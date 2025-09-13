package com.example.momentix.domain.reservation.service;

import com.example.momentix.domain.reservation.dto.ReservationResponseDto;
import com.example.momentix.domain.reservation.repository.ReservationRepository;
import com.example.momentix.domain.ticket.dto.ReservationDetailResponse;
import com.example.momentix.domain.ticket.dto.ReservationListItemResponse;
import com.example.momentix.domain.ticket.entity.Tickets;
import com.example.momentix.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationReadService {
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    public List<ReservationListItemResponse> getMyReservations(Long userId) {
        return ticketRepository.findByUsers_UserIdOrderByTicketIdDesc(userId)
                .stream().map(this::toListItem).toList();
    }

    public ReservationDetailResponse getMyReservationDetail(Long userId, Long ticketId) {
        Tickets t = ticketRepository.findByTicketIdAndUsers_UserId(ticketId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "티켓을 찾을 수 없음"));
        return toDetail(t);
    }

    private ReservationListItemResponse toListItem(Tickets t) {
        var et = t.getEventTime();
        var e  = et.getEvents();
        var s  = t.getSeat();

        String seatLabel = s.getSeatRow() + "-" + s.getSeatCol();
        LocalDateTime eventAt = et.getEventStartTime();

        return ReservationListItemResponse.builder()
                .ticketId(t.getTicketId())
                .ticketNumber(t.getTicketNumber())
                .ticketStatusType(t.getTicketStatusType())
                .eventId(t.getEventId() != null ? t.getEventId() : e.getId())
                .eventTitle(e.getEventTitle())
                .seatLabel(seatLabel)
                .eventAt(eventAt)
                .build();
    }

    private ReservationDetailResponse toDetail(Tickets t) {
        var et = t.getEventTime();
        var e  = et.getEvents();
        var s  = t.getSeat();
        var ph = t.getPaymentHistory();

        String seatLabel = s.getSeatRow() + "-" + s.getSeatCol();
        LocalDateTime eventAt = et.getEventStartTime();
        String placeName = s.getPlaces().getPlaceName();

        return ReservationDetailResponse.builder()
                .ticketId(t.getTicketId())
                .ticketNumber(t.getTicketNumber())
                .ticketStatus(t.getTicketStatusType().name())
                .eventId(t.getEventId() != null ? t.getEventId() : e.getId())
                .eventTitle(e.getEventTitle())
                .placeName(placeName)
                .seatLabel(seatLabel)
                .eventAt(eventAt)
                .paymentPrice(ph != null ? ph.getPaymentPrice() : null)
                .paymentStatus(ph != null ? ph.getPaymentStatus().name() : null)
                .build();
    }

@Transactional(readOnly = true)
    public ReservationResponseDto getReservationSimple(Long userId, Long eventId) {
        var r = reservationRepository.findByReservationIdAndUsers_UserId(eventId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "예약을 찾을 수 없음"));
        return ReservationResponseDto.fromWithEvent(r);
    }
}
