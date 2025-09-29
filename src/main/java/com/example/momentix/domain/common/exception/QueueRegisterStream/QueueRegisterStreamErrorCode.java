package com.example.momentix.domain.common.exception.QueueRegisterStream;

import com.example.momentix.domain.common.exception.ErrorException;
import com.example.momentix.domain.common.exception.event.EventErrorCode;

public class QueueRegisterStreamErrorCode extends ErrorException {
    public QueueRegisterStreamErrorCode(EventErrorCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
