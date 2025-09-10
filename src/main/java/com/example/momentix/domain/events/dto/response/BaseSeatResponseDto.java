package com.example.momentix.domain.events.dto.response;

import lombok.Getter;

@Getter
public class BaseSeatResponseDto {
//    private String placeName; 추후 공연장별 구분용으로 추가 가능
    private int seatRow;
    private int seatCol;

}
