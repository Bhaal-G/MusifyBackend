package com.nb.musify.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {

        super(message);
    }
}
