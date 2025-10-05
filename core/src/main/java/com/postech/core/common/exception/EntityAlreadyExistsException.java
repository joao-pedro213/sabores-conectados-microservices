package com.postech.core.common.exception;

public class EntityAlreadyExistsException extends BusinessException {
    private static final String MESSAGE = "The %s with the provided identifier already exists";

    public EntityAlreadyExistsException(String entityName) {
        super(MESSAGE.formatted(entityName));
    }
}
