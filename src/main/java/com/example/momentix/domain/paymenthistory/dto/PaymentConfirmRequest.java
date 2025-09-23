package com.example.momentix.domain.paymenthistory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentConfirmRequest {
    private final Long reservationId;

    @JsonCreator
    public PaymentConfirmRequest(@JsonProperty("reservationId") Long reservationId){
        this.reservationId=reservationId;
    }

    public Long getReservationId(){return reservationId;}
}
