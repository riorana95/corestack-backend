package com.xora.backend.group.service;

import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.common.exception.ResourceNotFoundException;
import com.xora.backend.group.entity.GroupMemberEntity;
import com.xora.backend.group.enums.GroupMemberStatus;
import com.xora.backend.group.enums.GroupRole;
import com.xora.backend.group.repository.GroupMemberRepository;
import com.xora.backend.group.repository.GroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GroupAuthorizationService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupAuthorizationService(GroupRepository groupRepository,
                                     GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public void assertGroupExists(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found");
        }
    }

    public GroupMemberEntity requireActiveMembership(UUID groupId, UUID userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .filter(member -> member.getStatus() == GroupMemberStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.FORBIDDEN,
                        "You are not an active member of this group",
                        HttpStatus.FORBIDDEN));
    }

    public void assertAdmin(UUID groupId, UUID userId) {
        GroupMemberEntity membership = requireActiveMembership(groupId, userId);
        if (membership.getRole() != GroupRole.ADMIN) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Admin access required",
                    HttpStatus.FORBIDDEN);
        }
    }

    public GroupRole getActiveRole(UUID groupId, UUID userId) {
        return requireActiveMembership(groupId, userId).getRole();
    }
}
