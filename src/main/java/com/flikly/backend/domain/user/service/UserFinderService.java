package com.flikly.backend.domain.user.service;

import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.exception.UserNotFoundException;
import com.flikly.backend.domain.user.repository.UserRepository;
import com.flikly.backend.domain.user.vo.Email;
import com.flikly.backend.global.constant.ErrorMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 조회 관련 공통 로직을 처리하는 서비스
 */
@Service
public class UserFinderService {

    private final UserRepository userRepository;

    public UserFinderService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ID로 사용자를 조회
     *
     * @param userId 사용자 ID
     * @return 조회된 사용자 엔티티
     * @throws UserNotFoundException 사용자가 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.User.NOT_FOUND_BY_ID));
    }

    /**
     * 이메일로 사용자를 조회
     *
     * @param emailValue 이메일 문자열
     * @return 조회된 사용자 엔티티
     * @throws UserNotFoundException 사용자가 존재하지 않을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public User findUserByEmail(String emailValue) {
        Email email = new Email(emailValue);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.User.NOT_FOUND_BY_EMAIL));
    }
}