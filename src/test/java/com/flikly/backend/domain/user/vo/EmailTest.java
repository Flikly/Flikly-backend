package com.flikly.backend.domain.user.vo;

import com.flikly.backend.domain.user.exception.EmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Nested
    @DisplayName("이메일 생성 테스트")
    class CreateEmailTest {


        @Test
        @DisplayName("유효한 이메일 형식으로 객체 생성 성공")
        void createEmail_withValidFormat_succeeds() {
            // given
            String validEmail = "user@example.com";

            // when
            Email email = new Email(validEmail);

            // then
            assertThat(email.getValue()).isEqualTo(validEmail);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        void createEmail_withNullOrEmpty_throwsException(String emptyEmail) {

            // when & then
            assertThatThrownBy(() -> new Email(emptyEmail))
                    .isInstanceOf(EmailException.EmptyEmailException.class)
                    .hasMessageContaining("이메일은 빈 값일 수 없습니다");


        }

        @ParameterizedTest
        @ValueSource(strings = {
                "userexample.com",   // @ 기호 없음
                "user@",             // 도메인 없음
                "user@.com",         // 도메인 이름 없음
                "@example.com",      // 로컬 파트 없음
                "user@example",      // 최상위 도메인 없음
                "user@ex ample.com", // 공백 포함
                "user@exam_ple.com", // 도메인에 밑줄 포함
                "user@.example.com"  // 도메인 시작이 점(.)
        })
        @DisplayName("유효하지 않은 이메일 형식으로 객체 생성 시 예외 발생")
        void createEmail_withInvalidFormat_throwsException(String invalidEmail) {
            // when & then
            assertThatThrownBy(() -> new Email(invalidEmail))
                    .isInstanceOf(EmailException.InvalidEmailFormatException.class)
                    .hasMessageContaining("유효하지 않은 이메일 형식입니다");
        }
    }
}