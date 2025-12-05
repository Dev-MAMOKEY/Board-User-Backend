package com.board.backend.domain.post;

import com.board.backend.domain.post.dto.*;
import com.board.backend.domain.user.User;
import com.board.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    @Transactional
    public Long create(Long userId, PostCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                user
        );

        postRepository.save(post);
        return post.getId();
    }

    // 게시글 단일 조회
    public PostResponse get(Long postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        return new PostResponse(post);
    }

    // 전체 조회
    public List<PostResponse> getAll() {
        return postRepository.findAllByIsDeletedFalse()
                .stream()
                .map(PostResponse::new)
                .toList();
    }

    // 게시글 수정 (본인 글만 가능)
    @Transactional
    public void update(Long postId, Long userId, PostUpdateRequest request) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 글만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent());
    }

    // 게시글 삭제 (본인 글만)
    @Transactional
    public void delete(Long postId, Long userId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 글만 삭제할 수 있습니다.");
        }

        post.delete();
    }
}
