package com.flikly.backend.domain.user.dto;

import com.flikly.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
* 인증 결과 전송 객체
* - 인증 성공 시 사용자 정보와 함께 추가 정보(토큰 등)를 포함할 수 있음
    */
    @Getter
    @Builder
    public class AuthenticationResultDTO {
    private final UserDTO user;
    private final String token; // 필요에 따라 JWT 토큰 등 추가 가능
    private final boolean authenticated;

/**
    * 인증 성공 결과 생성
      */
      public static AuthenticationResultDTO success(User user, String token) {
      return AuthenticationResultDTO.builder()
      .user(UserDTO.from(user))
      .token(token)
      .authenticated(true)
      .build();
      }

/**
    * 인증 성공 결과 생성 (토큰 없는 버전)
      */
      public static AuthenticationResultDTO success(User user) {
      return AuthenticationResultDTO.builder()
      .user(UserDTO.from(user))
      .authenticated(true)
      .build();
      }
      }