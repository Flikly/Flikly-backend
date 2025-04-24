package com.flikly.backend.domain.user.exception;

import com.flikly.backend.global.exception.DomainException;

public abstract class UserException extends DomainException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}