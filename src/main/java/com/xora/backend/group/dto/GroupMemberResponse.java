package com.xora.backend.group.dto;

import com.xora.backend.group.enums.GroupMemberStatus;
import com.xora.backend.group.enums.GroupRole;

import java.time.Instant;
import java.util.UUID;

public record GroupMemberResponse(
        UUID id,
        UUID userId,
        String email,
        String displayName,
        GroupRole role,
        GroupMemberStatus status,
        Instant joinedAt
) {
}
