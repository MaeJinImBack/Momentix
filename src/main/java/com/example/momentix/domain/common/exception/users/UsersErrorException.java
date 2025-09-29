package com.example.momentix.domain.common.exception.users;

import com.example.momentix.domain.common.exception.ErrorException;

public class UsersErrorException extends ErrorException {
    public UsersErrorException(UsersCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
