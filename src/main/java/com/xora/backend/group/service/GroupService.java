package com.xora.backend.group.service;

import com.xora.backend.auth.entity.UserEntity;
import com.xora.backend.auth.enums.UserStatus;
import com.xora.backend.auth.repository.UserRepository;
import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.common.exception.ResourceNotFoundException;
import com.xora.backend.common.util.SecurityUtils;
import com.xora.backend.group.dto.*;
import com.xora.backend.group.entity.GroupEntity;
import com.xora.backend.group.entity.GroupMemberEntity;
import com.xora.backend.group.enums.GroupMemberStatus;
import com.xora.backend.group.enums.GroupRole;
import com.xora.backend.group.mapper.GroupMapper;
import com.xora.backend.group.repository.GroupMemberRepository;
import com.xora.backend.group.repository.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupAuthorizationService groupAuthorizationService;
    private final GroupMapper groupMapper;
    private final SecurityUtils securityUtils;

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserRepository userRepository,
                        GroupAuthorizationService groupAuthorizationService,
                        GroupMapper groupMapper,
                        SecurityUtils securityUtils) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.groupAuthorizationService = groupAuthorizationService;
        this.groupMapper = groupMapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public GroupDetailResponse createGroup(CreateGroupRequest request) {
        UUID creatorId = securityUtils.getCurrentUserId();
        UserEntity creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        GroupEntity group = new GroupEntity();
        group.setName(request.name().trim());
        group.setDescription(request.description() == null ? null : request.description().trim());
        group.setCurrencyCode(resolveCurrency(request.currencyCode()));
        group.setCreatedBy(creator);
        GroupEntity savedGroup = groupRepository.save(group);

        GroupMemberEntity adminMember = new GroupMemberEntity();
        adminMember.setGroup(savedGroup);
        adminMember.setUser(creator);
        adminMember.setRole(GroupRole.ADMIN);
        adminMember.setStatus(GroupMemberStatus.ACTIVE);
        groupMemberRepository.save(adminMember);

        return getGroup(savedGroup.getId());
    }

    @Transactional(readOnly = true)
    public List<GroupSummaryResponse> listMyGroups() {
        UUID userId = securityUtils.getCurrentUserId();
        return groupRepository.findAllByMemberStatus(userId, GroupMemberStatus.ACTIVE).stream()
                .map(group -> {
                    int count = groupMemberRepository
                            .findByGroupIdAndStatus(group.getId(), GroupMemberStatus.ACTIVE)
                            .size();
                    GroupRole role = groupAuthorizationService.getActiveRole(group.getId(), userId);
                    return groupMapper.toSummaryResponse(group, count, role);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupDetailResponse getGroup(UUID groupId) {
        UUID userId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, userId);

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        GroupRole myRole = groupAuthorizationService.getActiveRole(groupId, userId);
        List<GroupMemberResponse> members = groupMemberRepository
                .findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE)
                .stream()
                .map(groupMapper::toMemberResponse)
                .toList();

        return groupMapper.toDetailResponse(group, myRole, members);
    }

    @Transactional
    public GroupDetailResponse updateGroup(UUID groupId, UpdateGroupRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        groupAuthorizationService.assertAdmin(groupId, userId);

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        group.setName(request.name().trim());
        group.setDescription(request.description() == null ? null : request.description().trim());
        group.setCurrencyCode(request.currencyCode().toUpperCase());
        groupRepository.save(group);

        return getGroup(groupId);
    }

    @Transactional
    public GroupMemberResponse addMember(UUID groupId, AddMemberRequest request) {
        UUID actorId = securityUtils.getCurrentUserId();
        groupAuthorizationService.assertAdmin(groupId, actorId);

        UserEntity userToAdd = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this email"));

        if (userToAdd.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "User account is not active",
                    HttpStatus.CONFLICT);
        }

        if (groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                groupId, userToAdd.getId(), GroupMemberStatus.ACTIVE)) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "User is already a member of this group",
                    HttpStatus.CONFLICT);
        }

        GroupMemberEntity member = groupMemberRepository
                .findByGroupIdAndUserId(groupId, userToAdd.getId())
                .orElseGet(GroupMemberEntity::new);

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        member.setGroup(group);
        member.setUser(userToAdd);
        member.setRole(GroupRole.MEMBER);
        member.setStatus(GroupMemberStatus.ACTIVE);

        return groupMapper.toMemberResponse(groupMemberRepository.save(member));
    }

    @Transactional
    public void removeMember(UUID groupId, UUID targetUserId) {
        UUID actorId = securityUtils.getCurrentUserId();
        groupAuthorizationService.requireActiveMembership(groupId, actorId);

        boolean isSelf = actorId.equals(targetUserId);
        if (!isSelf) {
            groupAuthorizationService.assertAdmin(groupId, actorId);
        }

        GroupMemberEntity member = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getStatus() != GroupMemberStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Member is not active in this group",
                    HttpStatus.CONFLICT);
        }

        if (member.getRole() == GroupRole.ADMIN) {
            long adminCount = groupMemberRepository.countByGroupIdAndStatusAndRole(
                    groupId, GroupMemberStatus.ACTIVE, GroupRole.ADMIN);
            if (adminCount <= 1) {
                throw new BusinessException(
                        ErrorCode.CONFLICT,
                        "Cannot remove the last admin. Promote another admin first.",
                        HttpStatus.CONFLICT);
            }
        }

        member.setStatus(GroupMemberStatus.LEFT);
        groupMemberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse> searchUsersForInvite(UUID groupId, String query) {
        UUID actorId = securityUtils.getCurrentUserId();
        groupAuthorizationService.assertAdmin(groupId, actorId);

        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        return userRepository.searchActiveUsers(UserStatus.ACTIVE, query.trim()).stream()
                .filter(user -> !user.getId().equals(actorId))
                .filter(user -> !groupMemberRepository.existsByGroupIdAndUserIdAndStatus(
                        groupId, user.getId(), GroupMemberStatus.ACTIVE))
                .limit(10)
                .map(user -> new UserSearchResponse(user.getId(), user.getEmail(), user.getDisplayName()))
                .toList();
    }

    private String resolveCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return "INR";
        }
        return currencyCode.trim().toUpperCase();
    }
}
