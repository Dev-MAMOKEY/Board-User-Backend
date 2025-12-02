package com.boardProject.backend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 관리자 로그인
    Optional<User> findByLoginId(String loginId);

    // 카카오 로그인
    Optional<User> findBySnsId(String snsId);

    // 이메일 중복 확인
    Optional<User> findByEmail(String email);

    // 삭제되지 않은 유저 조회
    Optional<User> findByIdAndIsDeletedFalse(Long id);
}
