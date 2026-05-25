package com.corestack.backend.auth.entity;

import com.corestack.backend.auth.enums.AuthProviderType;
import com.corestack.backend.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "sw_user_auth_providers")
@Getter
@Setter
@NoArgsConstructor
public class UserAuthProviderEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProviderType provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;
}
