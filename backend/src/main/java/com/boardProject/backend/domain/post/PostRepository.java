package com.boardProject.backend.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 단일 조회 (삭제되지 않은 글만)
    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    // 특정 유저의 글
    List<Post> findAllByUserIdAndIsDeletedFalse(Long userId);

    // 전체 글 중 삭제되지 않은 것
    List<Post> findAllByIsDeletedFalse();
}