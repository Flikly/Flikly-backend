package com.flikly.backend.domain.user.dto;

import com.flikly.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 전송 객체
 */
@Getter
@Builder
public class UserDTO {
    private final Long id;
    private final String email;
    private final String name;

    /**
     * User 엔티티를 UserDTO로 변환
     */
    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail().getValue()) // Value Object에서 값 추출
                .name(user.getName().getValue())   // Value Object에서 값 추출
                .build();
    }
}