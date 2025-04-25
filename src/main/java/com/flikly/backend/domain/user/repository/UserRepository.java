package com.flikly.backend.domain.user.repository;

import com.flikly.backend.domain.user.entity.User;
import com.flikly.backend.domain.user.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일 값 객체
     * @return 사용자 (Optional)
     */
    Optional<User> findByEmail(Email email);
}
