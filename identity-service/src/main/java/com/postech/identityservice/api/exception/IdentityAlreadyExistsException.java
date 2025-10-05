package com.postech.identityservice.api.exception;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class IdentityAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Identity [id=%s] already exist";

    public IdentityAlreadyExistsException(UUID id) {
        super(MESSAGE.formatted(id.toString()));
    }
}
