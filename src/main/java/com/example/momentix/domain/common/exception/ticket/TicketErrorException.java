package com.example.momentix.domain.common.exception.ticket;

import com.example.momentix.domain.common.exception.ErrorException;

public class TicketErrorException extends ErrorException {
    public TicketErrorException(TicketCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
