package com.flikly.backend.domain.user.service;

import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.exception.PasswordException;
import com.flikly.backend.domain.user.policy.PasswordEncoder;
import com.flikly.backend.domain.user.policy.PasswordPolicy;
import com.flikly.backend.domain.user.vo.Password;
import org.springframework.stereotype.Service;

/**
 * 비밀번호 관련 공통 로직을 처리하는 서비스
 */
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicy passwordPolicy;

    public PasswordService(PasswordEncoder passwordEncoder, PasswordPolicy passwordPolicy) {
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicy = passwordPolicy;
    }

    /**
     * 평문 비밀번호를 검증하고 암호화하여 Password 객체로 반환
     *
     * @param rawPassword 평문 비밀번호
     * @return 암호화된 Password 객체
     */
    public Password encodePassword(String rawPassword) {
        // 비밀번호 정책 검증
        Password temp = new Password(rawPassword, passwordPolicy);
        // 비밀번호 암호화
        String encrypted = passwordEncoder.encrypt(temp.getValue());
        return Password.ofEncrypted(encrypted);
    }

    /**
     * 사용자의 저장된 비밀번호와 입력된 평문 비밀번호 일치 여부 검증
     *
     * @param user 사용자 엔티티
     * @param rawPassword 평문 비밀번호
     * @throws PasswordException.PasswordMismatchException 비밀번호 불일치시 예외 발생
     */
    public void verifyPassword(User user, String rawPassword) {
        String storedPassword = user.getPassword().getValue();
        if (!passwordEncoder.matches(rawPassword, storedPassword)) {
            throw new PasswordException.PasswordMismatchException();
        }
    }
}