package com.example.momentix.domain.paymenthistory.dto;


import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentResponse {
    private final Long paymentHistoryId;
    private final String status;
    private final BigDecimal paymentPrice;

    public PaymentResponse(Long paymentHistoryId, String status, BigDecimal paymentPrice) {
        this.paymentHistoryId = paymentHistoryId;
        this.status = status;
        this.paymentPrice = paymentPrice;
    }

    public static PaymentResponse of(PaymentHistory paymentHistory){
        return new PaymentResponse(
                paymentHistory.getPaymentHistoryId(),
                paymentHistory.getPaymentStatusType().name(),
                paymentHistory.getPaymentPrice()
        );
    }

    public Long getPaymentHistoryId(){return paymentHistoryId;}
    public String getStatus(){return status;}
    public BigDecimal getPaymentPrice(){return paymentPrice;}
}
