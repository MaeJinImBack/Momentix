package com.example.momentix.domain.point.entity;

public enum PointOperationType {
    EARN,               // 즉시 적립(잔액 +)
    USE,                // 사용(잔액 -)
    PENDING_EARN,       // 적립 예정(pending +)
    PENDING_RELEASE,    // 예정 해제(pending - → balance +)
    PENDING_CANCEL,     // 예정 취소(pending -)
    REFUND_USE          // 사용 환급(취소 시 balance +)
}
