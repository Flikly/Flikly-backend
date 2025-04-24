package com.flikly.backend.global.exception;

/**
 * 도메인 내에서 발생하는 모든 예외의 기본 클래스
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}