package com.flikly.backend.domain.user.service;

import com.flikly.backend.domain.user.dto.AuthenticationResultDTO;
import com.flikly.backend.domain.user.dto.PasswordChangeResultDTO;
import com.flikly.backend.domain.user.dto.RegistrationResultDTO;
import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.exception.PasswordException;
import com.flikly.backend.domain.user.exception.UserNotFoundException;
import com.flikly.backend.domain.user.vo.Email;
import com.flikly.backend.domain.user.vo.Name;
import com.flikly.backend.domain.user.vo.Password;
import com.flikly.backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 사용자 관련 핵심 비즈니스 로직을 처리하는 서비스
* DTO 패턴을 적용하여 엔티티 대신 전송 객체를 사용
  */
  @Service
  public class UserService {

  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final UserFinderService userFinderService;

  public UserService(UserRepository userRepository,
  PasswordService passwordService,
  UserFinderService userFinderService) {
  this.userRepository = userRepository;
  this.passwordService = passwordService;
  this.userFinderService = userFinderService;
  }

  /**
    * 사용자 등록
    *
    * @param emailValue 이메일
    * @param plainPassword 평문 비밀번호
    * @param nameValue 이름
    * @return 등록 결과 DTO
      */
      @Transactional
      public RegistrationResultDTO registerUser(String emailValue, String plainPassword, String nameValue) {
      try {
      // 값 객체 생성
      Email email = new Email(emailValue);
      Name name = new Name(nameValue);

           // 비밀번호 처리
           Password securePassword = passwordService.encodePassword(plainPassword);

           User user = User.builder()
                   .email(email)
                   .name(name)
                   .password(securePassword)
                   .build();

           User savedUser = userRepository.save(user);
           return RegistrationResultDTO.success(savedUser.getId());
      } catch (Exception e) {
      return RegistrationResultDTO.failure(e.getMessage());
      }
      }

  /**
    * 로그인 검증
    *
    * @param emailValue 이메일
    * @param plainPassword 평문 비밀번호
    * @return 인증 결과 DTO
      */
      @Transactional(readOnly = true)
      public AuthenticationResultDTO authenticateUser(String emailValue, String plainPassword) {
      try {
      // 사용자 조회
      User user = userFinderService.findUserByEmail(emailValue);

           // 비밀번호 검증
           passwordService.verifyPassword(user, plainPassword);
           
           // 인증 성공 - 필요에 따라 토큰 생성 로직 추가 가능
           return AuthenticationResultDTO.success(user);

      } catch (UserNotFoundException | PasswordException e) {
      // 실패 시 빈 객체 반환보다는 예외를 던지는 것이 더 명확함
      // 컨트롤러에서 @ExceptionHandler로 처리 가능
      throw e;
      }
      }

  /**
    * 비밀번호 변경
    *
    * @param userId 사용자 ID
    * @param currentPassword 현재 비밀번호
    * @param newPassword 새 비밀번호
    * @return 비밀번호 변경 결과 DTO
      */
      @Transactional
      public PasswordChangeResultDTO changePassword(Long userId, String currentPassword, String newPassword) {
      try {
      // 사용자 조회
      User user = userFinderService.findUserById(userId);

           // 현재 비밀번호 검증
           passwordService.verifyPassword(user, currentPassword);
           
           // 새 비밀번호 암호화
           Password secureNewPassword = passwordService.encodePassword(newPassword);
           
           // 비밀번호 변경
           user.changePassword(secureNewPassword);
           
           return PasswordChangeResultDTO.success();

      } catch (UserNotFoundException | PasswordException e) {
      return PasswordChangeResultDTO.failure(e.getMessage());
      }
      }
      }