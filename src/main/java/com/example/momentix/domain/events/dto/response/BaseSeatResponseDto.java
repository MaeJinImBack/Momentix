package com.example.momentix.domain.events.dto.response;

import lombok.Getter;

@Getter
public class BaseSeatResponseDto {
    private String placeName;
    private int seatRow;
    private int seatCol;

}
