package com.example.momentix.domain.events.dto.response;

import com.example.momentix.domain.events.entity.enums.SeatGradeType;
import com.example.momentix.domain.events.entity.enums.SeatPartType;
import com.example.momentix.domain.events.entity.enums.SeatStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ReserveSeatResponseDto {
    private Long id;
    private SeatGradeType seatGradeType;
    private SeatPartType seatPartType;
    private BigDecimal seatPrice;
    // true가 예매 가능한 상태
    private SeatStatusType seatReserveStatus;

}
