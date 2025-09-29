package com.example.momentix.domain.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationTimeResponseDto {
    private LocalDateTime reservationStartDate;
    private LocalDateTime reservationEndDate;

}
