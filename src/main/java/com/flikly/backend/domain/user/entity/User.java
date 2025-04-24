package com.flikly.backend.domain.user.entity;

import com.flikly.backend.domain.user.vo.Email;
import com.flikly.backend.domain.user.vo.Name;
import com.flikly.backend.domain.user.vo.Password;
import com.flikly.backend.global.common.BaseEntity;
import com.flikly.backend.global.common.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 요구사항
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Email email;

    @Column(nullable = false)
    private Password password;

    @Column(nullable = false)
    private Name name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(Email email, Password password, Name name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

}
