package com.example.momentix.domain.common.exception.event;

import com.example.momentix.domain.common.exception.ErrorException;

public class EventErrorException extends ErrorException {
    public EventErrorException(EventErrorCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
