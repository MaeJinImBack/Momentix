package com.example.momentix.domain.events.entity.enums;

public enum SeatGradeType {
    V("VIP"),
    R("Royal"),
    S("Superior"),
    A("AGrade");

    private String seatGrade;

    SeatGradeType(String seatGrade) {
        this.seatGrade = seatGrade;
    }
}
