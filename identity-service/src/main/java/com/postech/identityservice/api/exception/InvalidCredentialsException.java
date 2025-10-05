package com.postech.identityservice.api.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("The username and / or password are invalid");
    }
}
