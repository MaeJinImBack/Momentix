package com.example.momentix.domain.common.exception.point;

import com.example.momentix.domain.common.exception.ErrorException;

public class PointErrorException extends ErrorException {
    public PointErrorException(PointCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
