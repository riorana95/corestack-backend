package com.corestack.backend.expense.dto;

import java.util.List;

public record ExpensePageResponse(
        List<ExpenseResponse> data,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
