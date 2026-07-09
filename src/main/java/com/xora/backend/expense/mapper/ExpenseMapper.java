package com.xora.backend.expense.mapper;

import com.xora.backend.expense.dto.ExpenseResponse;
import com.xora.backend.expense.dto.ExpenseSplitResponse;
import com.xora.backend.expense.entity.ExpenseEntity;
import com.xora.backend.expense.entity.ExpenseSplitEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(ExpenseEntity expense) {
        List<ExpenseSplitResponse> splits = expense.getSplits().stream()
                .map(this::toSplitResponse)
                .toList();
        return new ExpenseResponse(
                expense.getId(),
                expense.getGroup().getId(),
                expense.getPaidBy().getId(),
                expense.getPaidBy().getDisplayName(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCurrencyCode(),
                expense.getExpenseDate(),
                expense.getSplitType(),
                splits,
                expense.getCreatedAt());
    }

    private ExpenseSplitResponse toSplitResponse(ExpenseSplitEntity split) {
        return new ExpenseSplitResponse(
                split.getId(),
                split.getUser().getId(),
                split.getUser().getDisplayName(),
                split.getShareAmount(),
                split.getSharePercent());
    }
}
