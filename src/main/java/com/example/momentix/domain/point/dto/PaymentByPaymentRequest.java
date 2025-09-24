package com.example.momentix.domain.point.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentByPaymentRequest {
    private final String idempotencyKey;
    private final Long paymentId;

    @JsonCreator
    public PaymentByPaymentRequest(@JsonProperty("idempotencyKey") String idempotencyKey,
                                   @JsonProperty("paymentId") Long paymentId) {
        this.idempotencyKey = idempotencyKey;
        this.paymentId = paymentId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Long getPaymentId() {
        return paymentId;
    }
}
