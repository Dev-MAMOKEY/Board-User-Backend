package com.board.backend.domain.user;

import com.board.backend.domain.user.dto.UserResponseDto;
import com.board.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/user/me")
    public UserResponseDto getMyInfo() {
        Long userId = SecurityUtil.getCurrentUserId();

        if (userId == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        return userService.getUserInfo(userId);
    }
}
