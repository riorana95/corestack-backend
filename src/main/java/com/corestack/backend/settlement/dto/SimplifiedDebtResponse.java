package com.corestack.backend.settlement.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SimplifiedDebtResponse(
        UUID fromUserId,
        String fromUserDisplayName,
        UUID toUserId,
        String toUserDisplayName,
        BigDecimal amount
) {
}
