package com.xora.backend.auth.repository;

import com.xora.backend.auth.entity.UserAuthProviderEntity;
import com.xora.backend.auth.enums.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProviderEntity, UUID> {

    Optional<UserAuthProviderEntity> findByProviderAndProviderUserId(
            AuthProviderType provider,
            String providerUserId);
}
