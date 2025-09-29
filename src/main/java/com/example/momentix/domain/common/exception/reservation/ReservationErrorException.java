package com.example.momentix.domain.common.exception.reservation;

import com.example.momentix.domain.common.exception.ErrorException;

public class ReservationErrorException extends ErrorException {
    public ReservationErrorException(ReservationErrorCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
