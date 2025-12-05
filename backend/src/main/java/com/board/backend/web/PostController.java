package com.board.backend.web;

import com.board.backend.domain.post.PostService;
import com.board.backend.domain.post.dto.*;
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
    public Long create(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody PostCreateRequest request
    ) {
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
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request
    ) {
        postService.update(postId, userId, request);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void delete(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long postId
    ) {
        postService.delete(postId, userId);
    }
}
