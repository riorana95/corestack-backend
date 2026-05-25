package com.corestack.backend.settlement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSettlementRequest(
        UUID fromUserId,
        @NotNull UUID toUserId,
        @NotNull @DecimalMin(value = "0.0001") BigDecimal amount,
        @Size(max = 500) String note
) {
}
