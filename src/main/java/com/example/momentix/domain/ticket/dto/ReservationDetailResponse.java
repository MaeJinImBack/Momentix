package com.example.momentix.domain.ticket.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationDetailResponse {
    private Long ticketId;
    private String ticketNumber;
    private String ticketStatus;
    private Long eventId;
    private String eventTitle;
    private String placeName;
    private String seatLabel;
    private LocalDateTime eventAt;
    private BigDecimal paymentPrice;
    private String paymentStatus;
}
