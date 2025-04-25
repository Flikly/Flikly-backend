package com.flikly.backend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
* 비밀번호 변경 결과 전송 객체
  */
  @Getter
  @Builder
  public class PasswordChangeResultDTO {
  private final boolean success;
  private final String message;

  /**
    * 변경 성공 결과 생성
      */
      public static PasswordChangeResultDTO success() {
      return PasswordChangeResultDTO.builder()
      .success(true)
      .message("비밀번호가 성공적으로 변경되었습니다.")
      .build();
      }

  /**
    * 변경 실패 결과 생성
      */
      public static PasswordChangeResultDTO failure(String errorMessage) {
      return PasswordChangeResultDTO.builder()
      .success(false)
      .message(errorMessage)
      .build();
      }
      }