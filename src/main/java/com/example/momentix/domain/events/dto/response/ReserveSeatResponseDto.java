package com.example.momentix.domain.events.dto.response;

import java.math.BigDecimal;

public class ReserveSeatResponseDto {
    private Long id;
    private String seatGrade;
    private String seatPart;
    private BigDecimal seatPrice;
    // true가 예매 가능한 상태
    private boolean seatStatus;

}
