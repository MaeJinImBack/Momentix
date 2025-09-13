package com.example.momentix.domain.ticket.dto.request;

import com.example.momentix.domain.ticket.entity.TicketStatusType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTicketStatusRequestDto {
    private TicketStatusType ticketStatus;
}