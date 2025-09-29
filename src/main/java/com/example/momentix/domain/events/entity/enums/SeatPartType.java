package com.example.momentix.domain.events.entity.enums;

import lombok.Getter;

@Getter
public enum SeatPartType {
    A("A구역", 1L),
    B("B구역", 2L),
    C("C구역", 3L),
    D("D구역", 4L),
    E("E구역", 5L);

    private String seatPart;
    private Long id;

    SeatPartType(String seatPart, Long id) {
        this.seatPart = seatPart;
        this.id = id;
    }
    public static SeatPartType fromId(Long id) {
        for (SeatPartType type : SeatPartType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No SeatPartType with id " + id);
    }
}
