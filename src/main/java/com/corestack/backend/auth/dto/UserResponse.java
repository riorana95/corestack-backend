package com.corestack.backend.auth.dto;

import com.corestack.backend.auth.enums.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        UserStatus status
) {
}
