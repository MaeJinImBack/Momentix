package com.example.momentix.domain.events.entity.enums;

import lombok.Getter;

@Getter
public enum AgeRatingType {
    ALL(0, "전체 이용가"),
    AGE_12(12, "12세 이상 관람가"),
    AGE_15(15, "15세 이상 관람가"),
    AGE_19(19, "청소년 관람불가");

    // 나이
    private final int age;
    // 나이별 관람 제한 설명
    private final String description;

    AgeRatingType(int age, String description) {
        this.age = age;
        this.description = description;
    }

    // 나이에 맞는 관람제한 설명
    public static AgeRatingType ratingByAge(int age) {
        for (AgeRatingType rating : AgeRatingType.values()) {
            if (rating.getAge() == age) {
                return rating;
            }
        }
        throw new IllegalArgumentException("not found ageRatingType");
    }
}
