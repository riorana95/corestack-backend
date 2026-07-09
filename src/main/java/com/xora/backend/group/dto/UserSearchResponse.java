package com.xora.backend.group.dto;

import java.util.UUID;

public record UserSearchResponse(
        UUID id,
        String email,
        String displayName
) {
}
