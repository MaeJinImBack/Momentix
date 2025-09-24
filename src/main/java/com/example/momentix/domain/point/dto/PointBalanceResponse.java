package com.example.momentix.domain.point.dto;

// 사용자 현재 잔액/대기
public class PointBalanceResponse {
    private final Long userId;
    private final long pointBalance;
    private final long pointPending;

    public PointBalanceResponse(
            Long userId, long pointBalance, // 지금 당장 쓸 수 있는 확정 포인트
            long pointPending // 아직 확정되지 않은 적립 예정 포인트
    ) {
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
