package com.example.momentix.domain.point.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PointApplyRequest {
    private final String idempotencyKey;
    private final long amount;
    private final String reason;
    private final Long paymentId;
    private final Long reservationId;

    @JsonCreator
    public PointApplyRequest(@JsonProperty("idempotencyKey") String idempotencyKey,
                             @JsonProperty("amount") long amount,
                             @JsonProperty("reason") String reason,
                             @JsonProperty("paymentId") Long paymentId,
                             @JsonProperty("reservationId") Long reservationId) {
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
        this.reason = reason;
        this.paymentId = paymentId;
        this.reservationId = reservationId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public long getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
