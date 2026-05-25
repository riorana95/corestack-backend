package com.corestack.backend.settlement.repository;

import com.corestack.backend.settlement.entity.SettlementEntity;
import com.corestack.backend.settlement.enums.SettlementStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<SettlementEntity, UUID> {

    List<SettlementEntity> findByGroupIdAndStatus(UUID groupId, SettlementStatus status);

    @EntityGraph(attributePaths = {"fromUser", "toUser", "group"})
    List<SettlementEntity> findByGroupIdOrderByCreatedAtDesc(UUID groupId);

    @EntityGraph(attributePaths = {"fromUser", "toUser", "group"})
    Optional<SettlementEntity> findByIdAndGroupId(UUID id, UUID groupId);
}
