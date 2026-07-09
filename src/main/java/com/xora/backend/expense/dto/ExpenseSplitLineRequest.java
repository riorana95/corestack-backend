package com.xora.backend.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseSplitLineRequest(
        @NotNull UUID userId,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal shareAmount,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal sharePercent
) {
}
