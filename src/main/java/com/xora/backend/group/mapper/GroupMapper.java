package com.xora.backend.group.mapper;

import com.xora.backend.group.dto.GroupDetailResponse;
import com.xora.backend.group.dto.GroupMemberResponse;
import com.xora.backend.group.dto.GroupSummaryResponse;
import com.xora.backend.group.entity.GroupEntity;
import com.xora.backend.group.entity.GroupMemberEntity;
import com.xora.backend.group.enums.GroupRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMapper {

    public GroupMemberResponse toMemberResponse(GroupMemberEntity member) {
        return new GroupMemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getEmail(),
                member.getUser().getDisplayName(),
                member.getRole(),
                member.getStatus(),
                member.getJoinedAt());
    }

    public GroupSummaryResponse toSummaryResponse(
            GroupEntity group,
            int memberCount,
            GroupRole myRole) {
        return new GroupSummaryResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrencyCode(),
                memberCount,
                myRole,
                group.getUpdatedAt());
    }

    public GroupDetailResponse toDetailResponse(
            GroupEntity group,
            GroupRole myRole,
            List<GroupMemberResponse> members) {
        return new GroupDetailResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrencyCode(),
                group.getCreatedBy().getId(),
                myRole,
                members,
                group.getCreatedAt(),
                group.getUpdatedAt());
    }
}
