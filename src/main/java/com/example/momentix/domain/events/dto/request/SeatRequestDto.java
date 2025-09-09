package com.example.momentix.domain.events.dto.request;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.ConverterNumber;
import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;

import java.math.BigDecimal;

@Getter
public class SeatRequestDto {
    @CsvBindByName
    private String seatGradeType;
    @CsvBindByName(column = "seatPrice")
    private BigDecimal seatPrice;
    @CsvBindByName
    private String seatPartType;
    @CsvBindByName
    private int seatRow;
    @CsvBindByName
    private int seatCol;

}
