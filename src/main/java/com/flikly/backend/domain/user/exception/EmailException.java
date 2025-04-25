package com.flikly.backend.domain.user.exception;

import com.flikly.backend.global.constant.ErrorMessages;



/**
 * 이메일 관련 예외
 */
public class EmailException extends UserException {

    protected EmailException(String message) {
        super(message);
    }

    protected EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 이메일이 비어있을 때 발생하는 예외
     */
    public static class EmptyEmailException extends EmailException {
        public EmptyEmailException() {
            super(ErrorMessages.Email.EMPTY);
        }
    }

    /**
     * 이메일 형식이 유효하지 않을 때 발생하는 예외
     */
    public static class InvalidEmailFormatException extends EmailException {
        public InvalidEmailFormatException(String email) {
            super(ErrorMessages.Email.INVALID_FORMAT);
        }
    }
}