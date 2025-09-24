package com.example.momentix.domain.paymenthistory.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

// 낙관적 락으로 업데이트 충돌 방지
// idempotencyKey: 유니크(같은 요청 중복 생성 차단)
// (reservationId, paymentStatusType) UNIQUE: 동일 예약에서 같은 상태 중복 생성 차단
// 예약 1건에 SUCCESS 1건
@Table(
        name = "payment_history",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_payment_idempotency", columnNames = {"idempotency_key"}),
                @UniqueConstraint(name = "ux_reservation_status", columnNames = {"reservation_id", "payment_status"})
        }
)
@Entity
@Getter
public class PaymentHistory extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentHistoryId;

    @Version
    @Column(name = "version")
    private Long version;

    // 예약 ID, 티켓발급 시 필요한 키
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    //결제자
    @Column(name = "payer")
    private String payer;

    //결제 수단
    @Column(name = "payment_method")
    private String paymentMethod;

    //결제금액
    @Column(name = "payment_price", nullable = false)
    private BigDecimal paymentPrice;

    //결제 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatusType paymentStatusType;

    // 중복 생성 차단
    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    // 매개변수 없는 생성자 = No-Args-Constructor
    protected PaymentHistory() {
    }

    public static PaymentHistory create(Long reservationId, String payer, String method, BigDecimal price, String idempotencyKey) {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.reservationId = reservationId;
        paymentHistory.payer = payer;
        paymentHistory.paymentMethod = method;
        paymentHistory.paymentPrice = price == null ? BigDecimal.ZERO : price;
        paymentHistory.idempotencyKey = idempotencyKey;
        paymentHistory.paymentStatusType = PaymentStatusType.PENDING;
        return paymentHistory;
    }

    public void markSuccess() {
        this.paymentStatusType = PaymentStatusType.SUCCESS;
    }

    public void markFailed() {
        this.paymentStatusType = PaymentStatusType.FAILED;
    }

    public void markCancel() {
        this.paymentStatusType = PaymentStatusType.CANCEL;
    }

    public Long getPaymentHistoryId() {
        return paymentHistoryId;
    }

    public Long getVersion() {
        return version;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getPayer() {
        return payer;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getPaymentPrice() {
        return paymentPrice;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public PaymentStatusType getPaymentStatusType() {
        return paymentStatusType;
    }
}