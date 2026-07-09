package com.xora.backend.settlement.service;

import com.xora.backend.auth.entity.UserEntity;
import com.xora.backend.auth.repository.UserRepository;
import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.common.exception.ResourceNotFoundException;
import com.xora.backend.common.util.MoneyUtil;
import com.xora.backend.common.util.SecurityUtils;
import com.xora.backend.group.entity.GroupEntity;
import com.xora.backend.group.enums.GroupMemberStatus;
import com.xora.backend.group.enums.GroupRole;
import com.xora.backend.group.repository.GroupMemberRepository;
import com.xora.backend.group.repository.GroupRepository;
import com.xora.backend.group.service.GroupAuthorizationService;
import com.xora.backend.settlement.dto.CreateSettlementRequest;
import com.xora.backend.settlement.dto.SettlementResponse;
import com.xora.backend.settlement.entity.SettlementEntity;
import com.xora.backend.settlement.enums.SettlementStatus;
import com.xora.backend.settlement.mapper.SettlementMapper;
import com.xora.backend.settlement.repository.SettlementRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupAuthorizationService groupAuthorizationService;
    private final SettlementMapper settlementMapper;
    private final SecurityUtils securityUtils;

    public SettlementService(SettlementRepository settlementRepository,
                             GroupRepository groupRepository,
                             GroupMemberRepository groupMemberRepository,
                             UserRepository userRepository,
                             GroupAuthorizationService groupAuthorizationService,
                             SettlementMapper settlementMapper,
                             SecurityUtils securityUtils) {
        this.settlementRepository = settlementRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.groupAuthorizationService = groupAuthorizationService;
        this.settlementMapper = settlementMapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public SettlementResponse createSettlement(UUID groupId, CreateSettlementRequest request) {
        UUID actorId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, actorId);

        UUID fromUserId = request.fromUserId() != null ? request.fromUserId() : actorId;
        UUID toUserId = request.toUserId();

        if (fromUserId.equals(toUserId)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "Payer and recipient must be different users",
                    HttpStatus.BAD_REQUEST);
        }

        assertActiveMember(groupId, fromUserId);
        assertActiveMember(groupId, toUserId);

        if (!fromUserId.equals(actorId) && !toUserId.equals(actorId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "You must be either the payer or the recipient",
                    HttpStatus.FORBIDDEN);
        }

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        UserEntity fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));
        UserEntity toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        SettlementEntity settlement = new SettlementEntity();
        settlement.setGroup(group);
        settlement.setFromUser(fromUser);
        settlement.setToUser(toUser);
        settlement.setAmount(MoneyUtil.normalize(request.amount()));
        settlement.setNote(request.note() == null ? null : request.note().trim());
        settlement.setStatus(SettlementStatus.PENDING);

        SettlementEntity saved = settlementRepository.save(settlement);
        return settlementMapper.toResponse(
                settlementRepository.findByIdAndGroupId(saved.getId(), groupId)
                        .orElseThrow(() -> new ResourceNotFoundException("Settlement not found")));
    }

    @Transactional
    public SettlementResponse completeSettlement(UUID groupId, UUID settlementId) {
        SettlementEntity settlement = loadSettlementForAction(groupId, settlementId);
        if (settlement.getStatus() != SettlementStatus.PENDING) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Only pending settlements can be completed",
                    HttpStatus.CONFLICT);
        }

        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setSettledAt(Instant.now());
        settlementRepository.save(settlement);

        return settlementMapper.toResponse(
                settlementRepository.findByIdAndGroupId(settlementId, groupId)
                        .orElseThrow(() -> new ResourceNotFoundException("Settlement not found")));
    }

    @Transactional
    public SettlementResponse cancelSettlement(UUID groupId, UUID settlementId) {
        SettlementEntity settlement = loadSettlementForAction(groupId, settlementId);
        if (settlement.getStatus() != SettlementStatus.PENDING) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Only pending settlements can be cancelled",
                    HttpStatus.CONFLICT);
        }

        settlement.setStatus(SettlementStatus.CANCELLED);
        settlementRepository.save(settlement);

        return settlementMapper.toResponse(
                settlementRepository.findByIdAndGroupId(settlementId, groupId)
                        .orElseThrow(() -> new ResourceNotFoundException("Settlement not found")));
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> listSettlements(UUID groupId) {
        UUID userId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, userId);

        return settlementRepository.findByGroupIdOrderByCreatedAtDesc(groupId).stream()
                .map(settlementMapper::toResponse)
                .toList();
    }

    private SettlementEntity loadSettlementForAction(UUID groupId, UUID settlementId) {
        UUID actorId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, actorId);

        SettlementEntity settlement = settlementRepository.findByIdAndGroupId(settlementId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found"));

        boolean isParty = settlement.getFromUser().getId().equals(actorId)
                || settlement.getToUser().getId().equals(actorId);
        boolean isAdmin = groupAuthorizationService.getActiveRole(groupId, actorId) == GroupRole.ADMIN;

        if (!isParty && !isAdmin) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Only settlement parties or group admin can update this settlement",
                    HttpStatus.FORBIDDEN);
        }

        return settlement;
    }

    private void assertActiveMember(UUID groupId, UUID userId) {
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                groupId, userId, GroupMemberStatus.ACTIVE)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "User is not an active group member",
                    HttpStatus.FORBIDDEN);
        }
    }
}
