package com.corestack.backend.group.entity;

import com.corestack.backend.auth.entity.UserEntity;
import com.corestack.backend.common.entity.BaseAuditEntity;
import com.corestack.backend.group.enums.GroupMemberStatus;
import com.corestack.backend.group.enums.GroupRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sw_group_members")
@Getter
@Setter
@NoArgsConstructor
public class GroupMemberEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupRole role = GroupRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberStatus status = GroupMemberStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();
}
