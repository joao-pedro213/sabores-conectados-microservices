package com.postech.core.account.domain.exception;

import com.postech.core.common.exception.BusinessException;

public class InvalidEmailException extends BusinessException {
    private static final String MESSAGE = "Email should have a valid format";

    public InvalidEmailException() {
        super(MESSAGE);
    }
}
