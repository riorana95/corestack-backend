package com.xora.backend.group.repository;

import com.xora.backend.group.entity.GroupMemberEntity;
import com.xora.backend.group.enums.GroupMemberStatus;
import com.xora.backend.group.enums.GroupRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, UUID> {

    boolean existsByGroupIdAndUserIdAndStatus(UUID groupId, UUID userId, GroupMemberStatus status);

    Optional<GroupMemberEntity> findByGroupIdAndUserId(UUID groupId, UUID userId);

    @EntityGraph(attributePaths = {"user"})
    List<GroupMemberEntity> findByGroupIdAndStatus(UUID groupId, GroupMemberStatus status);

    boolean existsByGroupIdAndUserIdAndStatusAndRole(
            UUID groupId,
            UUID userId,
            GroupMemberStatus status,
            GroupRole role);

    long countByGroupIdAndStatusAndRole(UUID groupId, GroupMemberStatus status, GroupRole role);
}
