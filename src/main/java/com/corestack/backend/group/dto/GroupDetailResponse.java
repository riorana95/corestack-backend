package com.corestack.backend.group.dto;

import com.corestack.backend.group.enums.GroupRole;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GroupDetailResponse(
        UUID id,
        String name,
        String description,
        String currencyCode,
        UUID createdByUserId,
        GroupRole myRole,
        List<GroupMemberResponse> members,
        Instant createdAt,
        Instant updatedAt
) {
}
