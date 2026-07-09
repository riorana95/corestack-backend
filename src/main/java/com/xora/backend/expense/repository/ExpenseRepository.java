package com.xora.backend.expense.repository;

import com.xora.backend.expense.entity.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {

    @EntityGraph(attributePaths = {"paidBy", "splits", "splits.user"})
    Page<ExpenseEntity> findByGroupIdOrderByExpenseDateDescCreatedAtDesc(UUID groupId, Pageable pageable);

    @EntityGraph(attributePaths = {"paidBy", "splits", "splits.user", "group"})
    Optional<ExpenseEntity> findByIdAndGroupId(UUID id, UUID groupId);

    @EntityGraph(attributePaths = {"paidBy", "splits", "splits.user"})
    List<ExpenseEntity> findByGroupId(UUID groupId);
}
