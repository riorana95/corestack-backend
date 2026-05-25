package com.corestack.backend.expense.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MemberBalanceResponse(
        UUID userId,
        String displayName,
        String email,
        BigDecimal netBalance
) {
}
