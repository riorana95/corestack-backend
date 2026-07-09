package com.xora.backend.group.dto;

import com.xora.backend.group.enums.GroupRole;

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
