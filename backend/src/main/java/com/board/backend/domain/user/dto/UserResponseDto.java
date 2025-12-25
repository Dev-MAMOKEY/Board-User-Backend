package com.board.backend.domain.user.dto;

import com.board.backend.domain.user.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String snsId;
    private Role role;
}
