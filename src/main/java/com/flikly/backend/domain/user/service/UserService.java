package com.flikly.backend.domain.user.service;

import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.exception.UserNotFoundException;
import com.flikly.backend.domain.user.policy.PasswordEncoder;
import com.flikly.backend.domain.user.policy.PasswordPolicy;
import com.flikly.backend.domain.user.repository.UserRepository;
import com.flikly.backend.domain.user.vo.Email;
import com.flikly.backend.domain.user.vo.Name;
import com.flikly.backend.domain.user.vo.Password;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 생성자를 통한 의존성 주입
     * 의존성 역전 원칙(DIP)을 적용하여 구체적인 구현체가 아닌 인터페이스에 의존
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 사용자 등록 메서드
     *
     * @param emailValue 이메일
     * @param plainPassword 평문 비밀번호
     * @param nameValue 이름
     * @return 등록된 사용자 ID
     */
    public Long registerUser(String emailValue, String plainPassword,
                             String nameValue, PasswordPolicy policy) {

        // 1. 값 객체 생성 (각 객체 생성 시 유효성 검증이 자동으로 수행됨)
        Email email = new Email(emailValue);
        Name name = new Name(nameValue);

        // 2. 비밀번호 객체 생성 및 정책 검증
        Password rawPassword = new Password(plainPassword, policy);

        // 3. 비밀번호 암호화
        String encryptedValue = passwordEncoder.encrypt(rawPassword.getValue());
        Password securePassword = Password.ofEncrypted(encryptedValue);

        User user = User.builder()
                .email(email)
                .name(name)
                .password(securePassword)
                .build();

        User savedUser = userRepository.save(user); // 사용자 저장

        return savedUser.getId(); // Id 반환
    }

    /**
     * 로그인 검증 메서드
     *
     * @param emailValue 이메일
     * @param plainPassword 평문 비밀번호
     * @return 인증된 사용자
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */

//    @Transactional(readOnly = true)
//    public User authenticateUser(Long userId,String emailValue, String plainPassword) {
//
//        // 1. 사용자 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다 " + emailValue));
//
//
//
//    }


}
