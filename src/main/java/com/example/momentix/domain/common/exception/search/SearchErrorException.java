package com.example.momentix.domain.common.exception.search;

import com.example.momentix.domain.common.exception.ErrorException;

public class SearchErrorException extends ErrorException {
    public SearchErrorException(SearchCode authErrorCode) {
        super(authErrorCode.getStatus(), authErrorCode.getMessage());
    }
}
