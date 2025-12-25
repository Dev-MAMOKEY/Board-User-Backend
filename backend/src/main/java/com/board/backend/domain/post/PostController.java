package com.board.backend.domain.post;

import com.board.backend.domain.post.dto.*;
import com.board.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping
    public Long create(@RequestBody PostCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId(); // JWT에서 가져옴
        return postService.create(userId, request);
    }

    // 단일 조회
    @GetMapping("/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    // 전체 조회
    @GetMapping
    public List<PostResponse> getAll() {
        return postService.getAll();
    }

    // 수정
    @PutMapping("/{postId}")
    public void update(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        postService.update(postId, userId, request);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void delete(@PathVariable Long postId) {
        Long userId = SecurityUtil.getCurrentUserId();
        postService.delete(postId, userId);
    }
}
