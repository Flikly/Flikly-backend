package com.flikly.backend.domain.user.service;

import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.vo.Email;
import com.flikly.backend.domain.user.vo.Name;
import com.flikly.backend.domain.user.vo.Password;
import com.flikly.backend.domain.user.repository.UserRepository;
import com.flikly.backend.global.common.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 핵심 비즈니스 로직을 처리하는 서비스
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final UserFinderService userFinderService;

    /**
     * 생성자를 통한 의존성 주입
     * 의존성 역전 원칙(DIP)을 적용하여 구체적인 구현체가 아닌 인터페이스 또는 추상화된 서비스에 의존
     */
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
     * @return 등록된 사용자 ID
     */
    @Transactional
    public Long registerUser(String emailValue, String plainPassword, String nameValue) {
        // 값 객체 생성 (각 객체 생성 시 유효성 검증이 자동으로 수행됨)
        Email email = new Email(emailValue);
        Name name = new Name(nameValue);

        // 비밀번호 처리는 PasswordService에 위임
        Password securePassword = passwordService.encodePassword(plainPassword);

        User user = User.builder()
                .email(email)
                .name(name)
                .password(securePassword)
                .role(Role.USER)
                .build();


        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    /**
     * 로그인 검증
     *
     * @param emailValue 이메일
     * @param plainPassword 평문 비밀번호
     * @return 인증된 사용자
     */
    @Transactional(readOnly = true)
    public User authenticateUser(String emailValue, String plainPassword) {
        // 사용자 조회는 UserFinderService에 위임
        User user = userFinderService.findUserByEmail(emailValue);

        // 비밀번호 검증은 PasswordService에 위임
        passwordService.verifyPassword(user, plainPassword);

        // 인증 성공
        return user; // 추후 DTO Wrapping 필요성! 무조건!
    }

    /**
     * 비밀번호 변경
     *
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        // 사용자 조회는 UserFinderService에 위임
        User user = userFinderService.findUserById(userId);

        // 현재 비밀번호 검증은 PasswordService에 위임
        passwordService.verifyPassword(user, currentPassword);

        // 새 비밀번호 암호화는 PasswordService에 위임
        Password secureNewPassword = passwordService.encodePassword(newPassword);

        // 비밀번호 변경
        user.changePassword(secureNewPassword);
    }
}