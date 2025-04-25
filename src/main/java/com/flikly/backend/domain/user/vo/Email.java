package com.flikly.backend.domain.user.vo;

import com.flikly.backend.domain.user.exception.EmailException.EmptyEmailException;
import com.flikly.backend.domain.user.exception.EmailException.InvalidEmailFormatException;
import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
public class Email {

    // RFC 5322 기반 이메일 정규 표현식
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+" +
                    "(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+" +
                    "\\.)+[a-zA-Z]{2,7}$");

    private String value;

    /**
     * JPA를 위한 기본 생성자
     * 직접 호출하지 마세요.
     */
    protected Email() {
    }

    /**
     * 이메일 값으로 객체를 생성하는 생성자
     *
     * @param value 이메일 주소
     * @throws EmptyEmailException 이메일이 null이거나 빈 값인 경우
     * @throws InvalidEmailFormatException 이메일 형식이 유효하지 않은 경우
     */
    public Email(String value) {
        validateEmail(value);
        // 이메일은 항상 소문자로 정규화하여 저장 (대소문자 동등성 보장)
        this.value = value.toLowerCase();
    }

    /**
     * 이메일 형식을 검증하는 메서드
     *
     * @param email 검증할 이메일 주소
     * @throws EmptyEmailException 이메일이 null이거나 빈 값인 경우
     * @throws InvalidEmailFormatException 이메일 형식이 유효하지 않은 경우
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new EmptyEmailException();
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailFormatException(email);
        }
    }




}

