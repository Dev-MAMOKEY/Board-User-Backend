package com.board.backend.domain.post.dto;

import com.board.backend.domain.post.Post;
import lombok.Getter;

@Getter
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Long userId;

    public PostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt().toString();
        this.updatedAt = post.getUpdatedAt().toString();
        this.userId = post.getUser().getId();

    }
}
