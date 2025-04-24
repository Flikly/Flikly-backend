package com.flikly.backend.domain.user.exception;

import com.flikly.backend.global.constant.ErrorMessages;



/**
 * 이메일 관련 예외
 */
public class EmailException extends UserException {

    public EmailException(String message) {
        super(message);
    }

    public static class EmptyEmailException extends EmailException {
        public EmptyEmailException() {
            super(ErrorMessages.Email.EMPTY);
        }
    }

    public static class InvalidEmailFormatException extends EmailException {
        public InvalidEmailFormatException(String email) {
            super(ErrorMessages.Email.INVALID_FORMAT);
        }
    }
}