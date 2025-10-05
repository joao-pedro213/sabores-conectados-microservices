package com.postech.core.common.exception;

public class EntityNotFoundException extends BusinessException {
    private static final String MESSAGE = "The %s with the provided identifier was not found";

    public EntityNotFoundException(String entityName) {
        super(MESSAGE.formatted(entityName));
    }
}
