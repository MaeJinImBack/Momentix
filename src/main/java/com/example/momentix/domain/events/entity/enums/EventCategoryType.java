package com.example.momentix.domain.events.entity.enums;

import lombok.Getter;

@Getter
public enum EventCategoryType {
    MUSICAL("뮤지컬"),
    CONCERT("콘서트"),
    CLASSIC("클래식"),
    PLAY("연극");

    private final String eventGenre;


    EventCategoryType(String eventCategory) {
        this.eventGenre = eventCategory;
    }
}
