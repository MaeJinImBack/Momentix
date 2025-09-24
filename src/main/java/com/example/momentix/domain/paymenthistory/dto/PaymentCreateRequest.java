package com.example.momentix.domain.paymenthistory.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentCreateRequest {
    private final Long reservationId;
    private final String payer;
    private final String paymentMethod;
    private final BigDecimal paymentPrice;
    private final String idempotencyKey;// 같은 요청 재전송/더블클릭/네트워크 재시도로도 중복 생성방지용도

    @JsonCreator
    public PaymentCreateRequest(
            @JsonProperty("reservationId") Long reservationId,
            @JsonProperty("payer") String payer,
            @JsonProperty("paymentMethod") String paymentMethod,
            @JsonProperty("paymentPrice") BigDecimal paymentPrice,
            @JsonProperty("idempotencyKey") String idempotencyKey
    ) {
        this.reservationId = reservationId;
        this.payer = payer;
        this.paymentMethod = paymentMethod;
        this.paymentPrice = paymentPrice;
        this.idempotencyKey = idempotencyKey;
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

}
