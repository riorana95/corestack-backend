package com.corestack.backend.expense.service;

import com.corestack.backend.auth.entity.UserEntity;
import com.corestack.backend.auth.repository.UserRepository;
import com.corestack.backend.common.exception.BusinessException;
import com.corestack.backend.common.exception.ErrorCode;
import com.corestack.backend.common.exception.ResourceNotFoundException;
import com.corestack.backend.common.util.MoneyUtil;
import com.corestack.backend.common.util.SecurityUtils;
import com.corestack.backend.expense.dto.*;
import com.corestack.backend.expense.engine.ExpenseSplitValidator;
import com.corestack.backend.expense.engine.SplitCalculator;
import com.corestack.backend.expense.engine.SplitLine;
import com.corestack.backend.expense.entity.ExpenseEntity;
import com.corestack.backend.expense.entity.ExpenseSplitEntity;
import com.corestack.backend.expense.enums.SplitType;
import com.corestack.backend.expense.mapper.ExpenseMapper;
import com.corestack.backend.expense.repository.ExpenseRepository;
import com.corestack.backend.group.entity.GroupEntity;
import com.corestack.backend.group.entity.GroupMemberEntity;
import com.corestack.backend.group.enums.GroupMemberStatus;
import com.corestack.backend.group.repository.GroupMemberRepository;
import com.corestack.backend.group.repository.GroupRepository;
import com.corestack.backend.group.service.GroupAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupAuthorizationService groupAuthorizationService;
    private final SplitCalculator splitCalculator;
    private final ExpenseSplitValidator expenseSplitValidator;
    private final ExpenseMapper expenseMapper;
    private final SecurityUtils securityUtils;

    public ExpenseService(ExpenseRepository expenseRepository,
                          GroupRepository groupRepository,
                          GroupMemberRepository groupMemberRepository,
                          UserRepository userRepository,
                          GroupAuthorizationService groupAuthorizationService,
                          SplitCalculator splitCalculator,
                          ExpenseSplitValidator expenseSplitValidator,
                          ExpenseMapper expenseMapper,
                          SecurityUtils securityUtils) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.groupAuthorizationService = groupAuthorizationService;
        this.splitCalculator = splitCalculator;
        this.expenseSplitValidator = expenseSplitValidator;
        this.expenseMapper = expenseMapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ExpenseResponse createExpense(UUID groupId, CreateExpenseRequest request) {
        UUID actorId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, actorId);

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Set<UUID> activeMemberIds = groupMemberRepository
                .findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE)
                .stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toSet());

        UUID payerId = request.paidByUserId() != null ? request.paidByUserId() : actorId;
        if (!activeMemberIds.contains(payerId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Payer must be an active group member",
                    HttpStatus.FORBIDDEN);
        }

        List<SplitLine> inputLines = toInputLines(request);
        expenseSplitValidator.validateSplitRequest(
                request.splitType(),
                request.participantUserIds(),
                inputLines);

        List<UUID> participantIds = resolveParticipantIds(request, inputLines);
        expenseSplitValidator.validateParticipantsAreMembers(activeMemberIds, participantIds);

        BigDecimal total = MoneyUtil.normalize(request.amount());
        List<SplitLine> calculatedLines = splitCalculator.calculate(
                request.splitType(),
                total,
                request.participantUserIds(),
                inputLines);

        UserEntity payer = userRepository.findById(payerId)
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));

        ExpenseEntity expense = new ExpenseEntity();
        expense.setGroup(group);
        expense.setPaidBy(payer);
        expense.setDescription(request.description().trim());
        expense.setAmount(total);
        expense.setCurrencyCode(group.getCurrencyCode());
        expense.setExpenseDate(request.expenseDate() != null ? request.expenseDate() : LocalDate.now());
        expense.setSplitType(request.splitType());

        for (SplitLine line : calculatedLines) {
            UserEntity user = userRepository.findById(line.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("Split user not found"));
            ExpenseSplitEntity split = new ExpenseSplitEntity();
            split.setExpense(expense);
            split.setUser(user);
            split.setShareAmount(line.shareAmount());
            split.setSharePercent(line.sharePercent());
            expense.getSplits().add(split);
        }

        ExpenseEntity saved = expenseRepository.save(expense);
        return expenseMapper.toResponse(
                expenseRepository.findByIdAndGroupId(saved.getId(), groupId)
                        .orElseThrow(() -> new ResourceNotFoundException("Expense not found")));
    }

    @Transactional(readOnly = true)
    public ExpensePageResponse listExpenses(UUID groupId, int page, int size) {
        UUID userId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, userId);

        Page<ExpenseEntity> expensePage = expenseRepository.findByGroupIdOrderByExpenseDateDescCreatedAtDesc(
                groupId,
                PageRequest.of(page, size));

        List<ExpenseResponse> data = expensePage.getContent().stream()
                .map(expenseMapper::toResponse)
                .toList();

        return new ExpensePageResponse(
                data,
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.getTotalElements(),
                expensePage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpense(UUID groupId, UUID expenseId) {
        UUID userId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, userId);

        ExpenseEntity expense = expenseRepository.findByIdAndGroupId(expenseId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        return expenseMapper.toResponse(expense);
    }

    private List<UUID> resolveParticipantIds(CreateExpenseRequest request, List<SplitLine> inputLines) {
        if (request.splitType() == SplitType.EQUAL) {
            return request.participantUserIds();
        }
        return inputLines.stream().map(SplitLine::userId).toList();
    }

    private List<SplitLine> toInputLines(CreateExpenseRequest request) {
        if (request.splits() == null || request.splits().isEmpty()) {
            return List.of();
        }
        return request.splits().stream()
                .map(split -> new SplitLine(split.userId(), split.shareAmount(), split.sharePercent()))
                .toList();
    }
}
