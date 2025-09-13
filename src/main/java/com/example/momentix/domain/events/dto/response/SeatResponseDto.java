package com.example.momentix.domain.events.dto.response;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SeatResponseDto {
    @CsvBindByName
    private String seatGradeType;
    @CsvBindByName(column = "seatPrice")
    private BigDecimal seatPrice;
    @CsvBindByName
    private String seatPartType;
    @CsvBindByName
    private Long seatRow;
    @CsvBindByName
    private Long seatCol;

}
