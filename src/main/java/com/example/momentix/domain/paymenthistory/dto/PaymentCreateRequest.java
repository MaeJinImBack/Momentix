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

    @JsonCreator
    public PaymentCreateRequest(
            @JsonProperty("reservationId") Long reservationId,
            @JsonProperty("payer") String payer,
            @JsonProperty("paymentMethod") String paymentMethod,
            @JsonProperty("paymentPrice") BigDecimal paymentPrice
    ) {
        this.reservationId = reservationId;
        this.payer = payer;
        this.paymentMethod = paymentMethod;
        this.paymentPrice = paymentPrice;
    }

    public Long getReservationId(){return reservationId;}
    public String getPayer(){return payer;}
    public String getPaymentMethod(){return paymentMethod;}
    public BigDecimal getPaymentPricel(){return paymentPrice;}
}
