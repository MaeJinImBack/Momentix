package com.example.momentix.domain.common.exception.paymenthistory;

import com.example.momentix.domain.common.exception.ErrorException;

public class PaymentHistoryErrorException extends ErrorException {
    public PaymentHistoryErrorException(PaymentHistoryCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
