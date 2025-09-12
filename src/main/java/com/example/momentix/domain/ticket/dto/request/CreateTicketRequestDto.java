package com.example.momentix.domain.ticket.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTicketRequestDto {
    private Long reservationId;
}