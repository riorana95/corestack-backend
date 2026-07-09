package com.xora.backend.expense.engine;

import java.math.BigDecimal;
import java.util.UUID;

public record SplitLine(
        UUID userId,
        BigDecimal shareAmount,
        BigDecimal sharePercent
) {
}
