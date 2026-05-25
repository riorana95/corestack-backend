package com.corestack.backend.group.dto;

import com.corestack.backend.group.enums.GroupRole;

import java.time.Instant;
import java.util.UUID;

public record GroupSummaryResponse(
        UUID id,
        String name,
        String description,
        String currencyCode,
        int memberCount,
        GroupRole myRole,
        Instant updatedAt
) {
}
