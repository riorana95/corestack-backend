package com.corestack.backend.expense.controller;

import com.corestack.backend.expense.dto.CreateExpenseRequest;
import com.corestack.backend.expense.dto.ExpensePageResponse;
import com.corestack.backend.expense.dto.ExpenseResponse;
import com.corestack.backend.expense.dto.GroupBalancesResponse;
import com.corestack.backend.expense.service.BalanceService;
import com.corestack.backend.expense.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final BalanceService balanceService;

    public ExpenseController(ExpenseService expenseService, BalanceService balanceService) {
        this.expenseService = expenseService;
        this.balanceService = balanceService;
    }

    @PostMapping("/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.createExpense(groupId, request);
    }

    @GetMapping("/expenses")
    public ExpensePageResponse listExpenses(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return expenseService.listExpenses(groupId, page, size);
    }

    @GetMapping("/expenses/{expenseId}")
    public ExpenseResponse getExpense(
            @PathVariable UUID groupId,
            @PathVariable UUID expenseId) {
        return expenseService.getExpense(groupId, expenseId);
    }

    @GetMapping("/balances")
    public GroupBalancesResponse getBalances(@PathVariable UUID groupId) {
        return balanceService.getGroupBalances(groupId);
    }
}
