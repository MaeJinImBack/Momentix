package com.example.momentix.domain.ticket.dto;


import com.example.momentix.domain.ticket.entity.TicketStatusType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationListItemResponse {
    private Long ticketId;
    private String ticketNumber;
    private TicketStatusType ticketStatusType;
    private Long eventId;
    private String eventTitle;
    private String seatLabel;       // 예: "A-16"
    private LocalDateTime eventAt;
}
