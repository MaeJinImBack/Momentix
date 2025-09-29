package com.example.momentix.domain.events.entity.enums;

import lombok.Getter;

@Getter
public enum SeatStatusType {
    AVAILABLE, // 예매 가능
    DISABLED, // 소프트 삭제
    HOLD, // 좌석 선점
    RESERVED // 예약된 좌석
}
