package com.corestack.backend.settlement.entity;

import com.corestack.backend.auth.entity.UserEntity;
import com.corestack.backend.common.entity.BaseAuditEntity;
import com.corestack.backend.group.entity.GroupEntity;
import com.corestack.backend.settlement.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sw_settlements")
@Getter
@Setter
@NoArgsConstructor
public class SettlementEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserEntity fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserEntity toUser;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(length = 500)
    private String note;

    @Column(name = "settled_at")
    private Instant settledAt;
}
