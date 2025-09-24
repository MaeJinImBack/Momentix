package com.example.momentix.domain.point.entity;


import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;

@Entity
@Table(name = "point_ledger")
public class PointLedger extends TimeStamped {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="idempotency_key", nullable=false, length=64)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name="operation_type", nullable=false, length=32)
    private PointOperationType pointOperationType;

    @Column(name="amount", nullable=false)
    private long amount;

    @Column(name="reason", length=128)
    private String reason;

    //결제/예약 매핑(느슨한 결합: FK없음, 포인트 테이블 독립임)
    @Column(name="related_payment_id")
    private Long relatedPaymentId;// 결제/예약과의 느슨한 연결

    @Column(name="related_reservation_id")
    private Long relatedReservationId;

    protected PointLedger() {}

    public PointLedger(Long userId, String idempotencyKey, PointOperationType pointOperationType,
                       long amount, String reason, Long relatedPaymentId, Long relatedReservationId) {
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.pointOperationType = pointOperationType;
        this.amount = amount;
        this.reason = reason;
        this.relatedPaymentId = relatedPaymentId;
        this.relatedReservationId = relatedReservationId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public PointOperationType getOperationType() { return pointOperationType; }
    public long getAmount() { return amount; }
    public String getReason() { return reason; }
    public Long getRelatedPaymentId() { return relatedPaymentId; }
    public Long getRelatedReservationId() { return relatedReservationId; }
}
