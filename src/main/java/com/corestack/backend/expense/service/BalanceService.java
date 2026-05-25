package com.corestack.backend.expense.service;

import com.corestack.backend.auth.entity.UserEntity;
import com.corestack.backend.common.exception.ResourceNotFoundException;
import com.corestack.backend.common.util.MoneyUtil;
import com.corestack.backend.common.util.SecurityUtils;
import com.corestack.backend.expense.dto.GroupBalancesResponse;
import com.corestack.backend.expense.dto.MemberBalanceResponse;
import com.corestack.backend.expense.entity.ExpenseEntity;
import com.corestack.backend.expense.entity.ExpenseSplitEntity;
import com.corestack.backend.expense.repository.ExpenseRepository;
import com.corestack.backend.group.entity.GroupEntity;
import com.corestack.backend.group.entity.GroupMemberEntity;
import com.corestack.backend.group.enums.GroupMemberStatus;
import com.corestack.backend.group.repository.GroupMemberRepository;
import com.corestack.backend.group.repository.GroupRepository;
import com.corestack.backend.group.service.GroupAuthorizationService;
import com.corestack.backend.settlement.entity.SettlementEntity;
import com.corestack.backend.settlement.enums.SettlementStatus;
import com.corestack.backend.settlement.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BalanceService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final GroupAuthorizationService groupAuthorizationService;
    private final SecurityUtils securityUtils;

    public BalanceService(GroupRepository groupRepository,
                          GroupMemberRepository groupMemberRepository,
                          ExpenseRepository expenseRepository,
                          SettlementRepository settlementRepository,
                          GroupAuthorizationService groupAuthorizationService,
                          SecurityUtils securityUtils) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
        this.groupAuthorizationService = groupAuthorizationService;
        this.securityUtils = securityUtils;
    }

    public GroupBalancesResponse getGroupBalances(UUID groupId) {
        UUID userId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, userId);

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        List<GroupMemberEntity> activeMembers =
                groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE);

        Map<UUID, BigDecimal> netByUser = new HashMap<>();
        Map<UUID, UserEntity> usersById = new HashMap<>();
        for (GroupMemberEntity member : activeMembers) {
            netByUser.put(member.getUser().getId(), BigDecimal.ZERO);
            usersById.put(member.getUser().getId(), member.getUser());
        }

        List<ExpenseEntity> expenses = expenseRepository.findByGroupId(groupId);
        for (ExpenseEntity expense : expenses) {
            UUID payerId = expense.getPaidBy().getId();
            netByUser.merge(payerId, expense.getAmount(), BigDecimal::add);
            for (ExpenseSplitEntity split : expense.getSplits()) {
                UUID splitUserId = split.getUser().getId();
                netByUser.merge(splitUserId, split.getShareAmount().negate(), BigDecimal::add);
                usersById.putIfAbsent(splitUserId, split.getUser());
            }
        }

        List<SettlementEntity> settlements =
                settlementRepository.findByGroupIdAndStatus(groupId, SettlementStatus.COMPLETED);
        for (SettlementEntity settlement : settlements) {
            netByUser.merge(settlement.getFromUser().getId(), settlement.getAmount().negate(), BigDecimal::add);
            netByUser.merge(settlement.getToUser().getId(), settlement.getAmount(), BigDecimal::add);
            usersById.putIfAbsent(settlement.getFromUser().getId(), settlement.getFromUser());
            usersById.putIfAbsent(settlement.getToUser().getId(), settlement.getToUser());
        }

        List<MemberBalanceResponse> balances = netByUser.entrySet().stream()
                .filter(entry -> usersById.containsKey(entry.getKey()))
                .map(entry -> {
                    UserEntity user = usersById.get(entry.getKey());
                    return new MemberBalanceResponse(
                            user.getId(),
                            user.getDisplayName(),
                            user.getEmail(),
                            MoneyUtil.normalize(entry.getValue()));
                })
                .sorted((a, b) -> a.displayName().compareToIgnoreCase(b.displayName()))
                .toList();

        return new GroupBalancesResponse(group.getId(), group.getCurrencyCode(), balances);
    }
}
