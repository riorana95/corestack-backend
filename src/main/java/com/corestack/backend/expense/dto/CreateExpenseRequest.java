package com.corestack.backend.expense.dto;

import com.corestack.backend.expense.enums.SplitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateExpenseRequest(
        @NotBlank @Size(max = 500) String description,
        @NotNull @DecimalMin(value = "0.0001") BigDecimal amount,
        UUID paidByUserId,
        LocalDate expenseDate,
        @NotNull SplitType splitType,
        List<UUID> participantUserIds,
        List<ExpenseSplitLineRequest> splits
) {
}
