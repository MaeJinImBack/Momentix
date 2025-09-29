package com.example.momentix.domain.common.exception.review;

import com.example.momentix.domain.common.exception.ErrorException;

public class ReviewErrorException extends ErrorException {
    public ReviewErrorException(ReviewCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
