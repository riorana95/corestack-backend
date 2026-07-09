package com.xora.backend.expense.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseSplitResponse(
        UUID id,
        UUID userId,
        String displayName,
        BigDecimal shareAmount,
        BigDecimal sharePercent
) {
}
