package com.example.momentix.domain.events.entity.enums;

public enum SeatPartType {
    A("A구역"),
    B("B구역"),
    C("C구역"),
    D("D구역"),
    E("E구역");

    private String seatPart;

    SeatPartType(String seatPart) {
        this.seatPart = seatPart;
    }
}
