package com.flikly.backend.domain.user.vo;

import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.exception.PasswordException;
import com.flikly.backend.domain.user.exception.PasswordException.PasswordEncryptionException;
import com.flikly.backend.domain.user.exception.UserNotFoundException;
import com.flikly.backend.domain.user.policy.DefaultPasswordPolicy;
import com.flikly.backend.domain.user.policy.PasswordEncoder;
import com.flikly.backend.domain.user.policy.PasswordPolicy;
import com.flikly.backend.domain.user.repository.UserRepository;
import com.flikly.backend.domain.user.service.BCryptPasswordEncoderImpl;
import com.flikly.backend.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.util.Predicates.isTrue;


class PasswordTest {


    @Nested      // 공통 테스트 그룹화
    @DisplayName("비밀번호 생성 테스트")
    class CreatePasswordTest {

        @Test
        @DisplayName("유효한 비밀번호로 객체 생성 성공")
        void createPasswordWithValidFormat() {

            // given
            PasswordPolicy policy = new DefaultPasswordPolicy();
            String validPassword = "Password123!";

            // when
            Password password = new Password(validPassword, policy);

            // then
            assertThat(password.getValue()).isEqualTo(validPassword);
            assertThat(password.isEncrypted()).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("비밀번호가 null이거나 빈 값이면 예외 발생")
        void throwExceptionForNullOrEmptyPassword(String invalidPassword) {

            // when & then
            PasswordPolicy policy = new DefaultPasswordPolicy();
            assertThatThrownBy(() -> new Password(invalidPassword, policy))
                    .isInstanceOf(PasswordException.EmptyPasswordException.class)
                    .hasMessageContaining("비밀번호는 필수 값입니다");
        }

        @Test
        @DisplayName("비밀번호 길이가 8자 미만이면 예외 발생")
        void throwExceptionForShortPassword() {

            // given
            String shortPassword = "Pass1!";
            PasswordPolicy policy = new DefaultPasswordPolicy();

            // then & when
            assertThatThrownBy(() -> new Password(shortPassword, policy))
                    .isInstanceOf(PasswordException.InvalidPasswordFormatException.class)
                    .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다");

        }

        @Test
        @DisplayName("비밀번호에 대문자가 없으면 예외 발생")
        void throwExceptionForPasswordWithoutUppercase() {

            // given
            String noUppercasePassword = "password123!";

            // when
            PasswordPolicy policy = new DefaultPasswordPolicy();

            // then
            assertThatThrownBy(() -> new Password(noUppercasePassword, policy))
                    .isInstanceOf(PasswordException.InvalidPasswordFormatException.class)
                    .hasMessageContaining("비밀번호는 적어도 하나의 대문자를 포함해야 합니다");

        }

        @Test
        @DisplayName("비밀번호에 숫자가 없으면 예외 발생")
        void throwExceptionForPasswordWithoutDigit() {

            // given
            String noDigitPassword = "Password!";

            // when
            PasswordPolicy policy = new DefaultPasswordPolicy();

            // then
            assertThatThrownBy(() -> new Password(noDigitPassword, policy))
                    .isInstanceOf(PasswordException.InvalidPasswordFormatException.class)
                    .hasMessageContaining("비밀번호는 적어도 하나의 숫자를 포함해야 합니다");
        }

        @Test
        @DisplayName("비밀번호에 특수문자가 없으면 예외 발생")
        void throwExceptionForPasswordWithoutSpecialChar() {

            // given
            String noSpecialCharPassword = "Password123";

            // when
            PasswordPolicy policy = new DefaultPasswordPolicy();

            // then
            assertThatThrownBy(() -> new Password(noSpecialCharPassword, policy))
                    .isInstanceOf(PasswordException.InvalidPasswordFormatException.class)
                    .hasMessageContaining("비밀번호는 적어도 하나의 특수문자를 포함해야 합니다");
        }
    }

    @Nested
    @DisplayName("비밀번호 일치 검증 테스트")
    class PasswordMatchingTest {

        @Test
        @DisplayName("암호화된 패스워드 생성 검증")
        void createEncryptedPassword() {
            // given
            String encryptedValue = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"; // 예시 해시값

            // when
            Password password = Password.ofEncrypted(encryptedValue);

            // then
            assertThat(password.getValue()).isEqualTo(encryptedValue);
            assertThat(password.isEncrypted()).isTrue();
        }

        @Test
        @DisplayName("암호화된 패스워드 생성 시 null 값이면 예외 발생")
        void throwExceptionForNullEncryptedPassword() {
            // given
            String nullEncryptedPassword = null;

            // when & then
            assertThatThrownBy(() -> Password.ofEncrypted(nullEncryptedPassword))
                    .isInstanceOf(PasswordEncryptionException.class)
                    .hasMessageContaining("암호화된 비밀번호는 필수 값입니다");
        }

        @Test
        @DisplayName("암호화된 패스워드 생성 시 빈 문자열이면 예외 발생")
        void throwExceptionForBlankEncryptedPassword() {
            // given
            String blankEncryptedPassword = "   ";

            // when & then
            assertThatThrownBy(() -> Password.ofEncrypted(blankEncryptedPassword))
                    .isInstanceOf(PasswordEncryptionException.class)
                    .hasMessageContaining("암호화된 비밀번호는 필수 값입니다");
        }

        @Test
        @DisplayName("잘못된 형식의 암호화 문자열이면 예외 발생")
        void invalidFormatThrowsException() {
            String invalid = "not-bcrypt";
            assertThatThrownBy(() -> Password.ofEncrypted(invalid))
                    .isInstanceOf(PasswordEncryptionException.class)
                    .hasMessageContaining("유효하지 않은 암호화된 비밀번호");
        }


        @Test
        @DisplayName("toString 메서드 - 암호화되지 않은 비밀번호")
        void toStringForRawPassword() {
            // given
            String validPassword = "Password123!";

            // when
            PasswordPolicy policy = new DefaultPasswordPolicy();
            Password password = new Password(validPassword, policy);

            // then
            assertThat(password.toString()).isEqualTo("[RAW]");
        }

        @Test
        @DisplayName("toString 메서드 - 암호화된 비밀번호")
        void toStringForEncryptedPassword() {
            // given
            String encryptedValue = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

            // when
            Password password = Password.ofEncrypted(encryptedValue);

            // then
            assertThat(password.toString()).isEqualTo("[ENCRYPTED]");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("암호화된 비밀번호가 null이거나 빈 값이면 예외 발생")
        void throwExceptionForNullOrEmptyEncryptedPassword(String invalidValue) {
            // when & then
            assertThatThrownBy(() -> Password.ofEncrypted(invalidValue))
                    .isInstanceOf(PasswordException.PasswordEncryptionException.class)
                    .hasMessageContaining("암호화된 비밀번호는 필수 값입니다");
        }
    }

    ///  기능 완성 후 통합 테스트로 빼야 함... 꼭!
    @Nested
    @DisplayName("비밀번호 일치 검증 통합 테스트")
    class PasswordMatchingIntegrationTest {

        private PasswordEncoder passwordEncoder;
        private PasswordPolicy passwordPolicy;
        private UserRepository userRepository;
        private UserService userService;

        @BeforeEach
        void setUp() {
            // 실제 구현체 생성
            passwordEncoder = new BCryptPasswordEncoderImpl(10);
            passwordPolicy = new DefaultPasswordPolicy();

            // Repository Mock 생성 및 설정
            userRepository = mock(UserRepository.class);

            // UserService 생성 (모든 필요한 매개변수 전달)
            userService = new UserService(userRepository, passwordEncoder, passwordPolicy);
        }

        @Test
        @DisplayName("BCrypt 암호화된 비밀번호 검증 성공")
        void passwordMatchesWithEncoder() {
            // given
            String rawPassword = "Password123!";
            String encryptedValue = passwordEncoder.encrypt(rawPassword);
            Password password = Password.ofEncrypted(encryptedValue);

            // when & then
            assertThat(passwordEncoder.matches(rawPassword, password.getValue()))
                    .isTrue();
        }

        @Test
        @DisplayName("비밀번호 불일치 시 예외 발생")
        void passwordMismatchThrowsException() {
            // given
            String correctPassword = "Password123!";
            String wrongPassword = "WrongPassword!";

            String encryptedValue = passwordEncoder.encrypt(correctPassword);
            Password password = Password.ofEncrypted(encryptedValue);

            User user = User.builder()
                    .id(1L)
                    .email(new Email("a@b.com"))
                    .name(new Name("tester"))
                    .password(password)
                    .build();

            // Mock 동작 설정
            when(userRepository.findByEmail(any(Email.class)))
                    .thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(
                    () -> userService.authenticateUser("a@b.com", wrongPassword)
            )
                    .isInstanceOf(PasswordException.PasswordMismatchException.class)
                    .hasMessageContaining("비밀번호가 일치하지 않습니다");
        }

        @Test
        @DisplayName("이메일로 사용자를 찾을 수 없을 때 예외 발생")
        void userNotFoundThrowsException() {
            // given
            when(userRepository.findByEmail(any(Email.class)))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                    () -> userService.authenticateUser("nonexistent@example.com", "anyPassword")
            )
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
