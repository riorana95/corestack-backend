package com.xora.backend.settlement.dto;

import com.xora.backend.settlement.enums.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SettlementResponse(
        UUID id,
        UUID groupId,
        UUID fromUserId,
        String fromUserDisplayName,
        UUID toUserId,
        String toUserDisplayName,
        BigDecimal amount,
        SettlementStatus status,
        String note,
        Instant settledAt,
        Instant createdAt
) {
}
