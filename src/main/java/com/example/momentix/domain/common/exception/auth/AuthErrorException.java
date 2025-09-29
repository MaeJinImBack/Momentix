package com.example.momentix.domain.common.exception.auth;

import com.example.momentix.domain.common.exception.ErrorException;

public class AuthErrorException extends ErrorException {
    public AuthErrorException(AuthErrorCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
