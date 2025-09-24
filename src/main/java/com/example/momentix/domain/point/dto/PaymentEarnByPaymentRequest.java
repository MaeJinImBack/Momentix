package com.example.momentix.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class PaymentEarnByPaymentRequest {
    private final String idempotencyKey;
    private final Long paymentId;
    private final Long reservationId;
    private final BigDecimal discountedAmount; // 할인 후 결제 금액
    @JsonCreator
    public PaymentEarnByPaymentRequest(
            @JsonProperty("idempotencyKey") String idempotencyKey,
            @JsonProperty("paymentId") Long paymentId,
            @JsonProperty("reservationId") Long reservationId,
            @JsonProperty("discountedAmount") BigDecimal discountedAmount
    ) {
        this.idempotencyKey = idempotencyKey;
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.discountedAmount = discountedAmount;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public Long getPaymentId() { return paymentId; }
    public Long getReservationId() { return reservationId; }
    public BigDecimal getDiscountedAmount() { return discountedAmount; }
}