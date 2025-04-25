package com.flikly.backend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
* 사용자 등록 결과 전송 객체
  */
  @Getter
  @Builder
  public class RegistrationResultDTO {
  private final Long userId;
  private final boolean success;
  private final String message;

  /**
    * 등록 성공 결과 생성
      */
      public static RegistrationResultDTO success(Long userId) {
      return RegistrationResultDTO.builder()
      .userId(userId)
      .success(true)
      .message("사용자가 성공적으로 등록되었습니다.")
      .build();
      }

  /**
    * 등록 실패 결과 생성
      */
      public static RegistrationResultDTO failure(String errorMessage) {
      return RegistrationResultDTO.builder()
      .success(false)
      .message(errorMessage)
      .build();
      }
      }