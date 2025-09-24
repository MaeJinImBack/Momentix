package com.example.momentix.domain.events.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationTimeResponseDto {
    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;

}
