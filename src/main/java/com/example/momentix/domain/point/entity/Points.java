package com.example.momentix.domain.point.entity;


import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Points extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "point_balance", nullable = false)
    private long pointBalance = 0l;

    @Column(name = "point_pending", nullable = false)
    private long pointPending = 0l;

    protected Points() {
    }

    public Points(Long userId, long pointBalance, long pointPending) {
        this.userId = userId;
        this.pointBalance = pointBalance;
        this.pointPending = pointPending;
    }

    public Long getPointId() {
        return pointId;
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

    // 캡슐화
    public void increaseBalance(long amount) {
        this.pointBalance += amount;
    }

    public void decreaseBalance(long amount) {
        this.pointBalance -= amount;
    }

    public void increasePending(long amount) {
        this.pointPending += amount;
    }

    public void decreasePending(long amount) {
        this.pointPending -= amount;
    }
}
