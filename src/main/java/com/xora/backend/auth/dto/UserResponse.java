package com.xora.backend.auth.dto;

import com.xora.backend.auth.enums.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        UserStatus status
) {
}
