package com.example.momentix.domain.ticket.dto.response;

import com.example.momentix.domain.ticket.entity.TicketStatusType;
import com.example.momentix.domain.ticket.entity.Tickets;
import lombok.Getter;

@Getter
public class TicketResponseDto {
    private Long ticketId;
    private String ticketNumber;
    private TicketStatusType ticketStatus;

    public TicketResponseDto(Tickets ticket) {
        this.ticketId = ticket.getTicketId();
        this.ticketNumber = ticket.getTicketNumber();
        this.ticketStatus = ticket.getTicketStatusType();
    }
}