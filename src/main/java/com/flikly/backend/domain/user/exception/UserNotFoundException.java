package com.flikly.backend.domain.user.exception;

/**
 * 사용자를 찾을 수 없는 경우 발생하는 예외
 */
public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
