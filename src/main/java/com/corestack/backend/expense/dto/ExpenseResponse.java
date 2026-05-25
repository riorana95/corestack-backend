package com.corestack.backend.expense.dto;

import com.corestack.backend.expense.enums.SplitType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID groupId,
        UUID paidByUserId,
        String paidByDisplayName,
        String description,
        BigDecimal amount,
        String currencyCode,
        LocalDate expenseDate,
        SplitType splitType,
        List<ExpenseSplitResponse> splits,
        Instant createdAt
) {
}
