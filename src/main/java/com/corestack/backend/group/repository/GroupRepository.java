package com.corestack.backend.group.repository;

import com.corestack.backend.group.entity.GroupEntity;
import com.corestack.backend.group.enums.GroupMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {

    @Query("""
            SELECT g FROM GroupEntity g
            JOIN GroupMemberEntity gm ON gm.group = g
            WHERE gm.user.id = :userId AND gm.status = :status
            ORDER BY g.updatedAt DESC
            """)
    List<GroupEntity> findAllByMemberStatus(
            @Param("userId") UUID userId,
            @Param("status") GroupMemberStatus status);
}
