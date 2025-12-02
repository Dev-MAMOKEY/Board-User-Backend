package com.boardProject.backend.domain.user;

import com.boardProject.backend.domain.auth.RefreshToken;
import com.boardProject.backend.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;  // LOCAL / KAKAO

    @Column(length = 255)
    private String password;    // 관리자 전용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;          // USER / ADMIN

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 100, unique = true)
    private String snsId;

    @Column(length = 50, unique = true)
    private String loginId;

    @Column(length = 255, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    // --- 연관관계 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
