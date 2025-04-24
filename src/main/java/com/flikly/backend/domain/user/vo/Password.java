package com.flikly.backend.domain.user.vo;
import com.flikly.backend.domain.user.exception.PasswordException;
import com.flikly.backend.domain.user.exception.PasswordException.PasswordEncryptionException;
import com.flikly.backend.domain.user.exception.PasswordException.EmptyPasswordException;
import com.flikly.backend.domain.user.policy.PasswordPolicy;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class Password {

    // 비밀번호 값을 저장하는 필드
    private String value;

    // 암호화 여부를 나타내는 필드
    private boolean encrypted;

    // JPA 기본 생성자
    protected Password() {
    }

    /**
     * 비밀번호 값을 반환하는 메서드
     *
     * @return 비밀번호 값 (암호화 여부에 따라 평문 또는 암호화된 값)
     */
    public String getValue() {
        return value;
    }

    /**
     * 비밀번호가 암호화되었는지 여부를 반환하는 메서드
     *
     * @return 암호화 여부 (true: 암호화됨, false: 평문)
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * 평문 비밀번호로 객체를 생성하는 생성자
     *
     * @param plainPassword 평문 비밀번호
     * @throws EmptyPasswordException 비밀번호가 null이거나 빈 값인 경우
     */
    public Password(String plainPassword, PasswordPolicy policy) {

        policy.validate(plainPassword);
        this.value = plainPassword;
        this.encrypted = false;
    }


    /**
     * 암호화된 비밀번호 생성 팩토리 메서드
     *
     * @param encryptedValue 암호화된 비밀번호 값
     * @return 암호화된 비밀번호 객체
     * @throws IllegalArgumentException 암호화된 값이 유효하지 않을 경우
     */
    public static Password ofEncrypted(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isBlank()) {
            throw new PasswordEncryptionException("암호화된 비밀번호는 필수 값입니다");
        }
        if (!encryptedValue.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$")) {
            throw new PasswordEncryptionException("유효하지 않은 암호화된 비밀번호 형식입니다");
        }
        return new Password(encryptedValue, true);
    }

    // 암호화된 비밀번호를 위한 private 생성자
    private Password(String value, boolean encrypted) {
        this.value = value;
        this.encrypted = encrypted;
    }


}
