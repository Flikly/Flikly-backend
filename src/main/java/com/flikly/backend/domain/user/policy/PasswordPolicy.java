package com.flikly.backend.domain.user.policy;

import com.flikly.backend.domain.user.exception.PasswordException;

/**
 * 비밀번호 검증을 위한 인터페이스
 *
 * 전략 패턴을 적용하여 다양한 검증을 사용할 수 있게 합니다.
 * 인터페이스 분리 원칙(ISP)을 준수하여 유효성 관련 기능만 정의합니다.
 */


public interface PasswordPolicy {
    void validate(String password) throws PasswordException;

    // 검증 결과를 true / false 로 반환
    boolean isValid(String password);
}
