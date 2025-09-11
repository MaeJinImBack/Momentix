package com.example.momentix.domain.reservation.entity;

// 삭제하고 PR
public enum SeatStatusType {
    AVAILABLE, // 예매 가능
    DISABLED, // 소프트 삭제
    HOLD, // 좌석 선점
    RESERVED // 예약된 좌석
}
