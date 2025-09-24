package com.example.momentix.domain.point.dto;

// 사용자 현재 잔액/대기
public class PointBalanceResponse {
    private final Long userId;
    private final long pointBalance;
    private final long pointPending;

    public PointBalanceResponse(Long userId, long pointBalance, long pointPending) {
        this.userId = userId;
        this.pointBalance = pointBalance;
        this.pointPending = pointPending;
    }

    public Long getUserId() {
        return userId;
    }

    public long getPointBalance() {
        return pointBalance;
    }

    public long getPointPending() {
        return pointPending;
    }
}
