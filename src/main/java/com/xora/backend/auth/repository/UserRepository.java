package com.xora.backend.auth.repository;

import com.xora.backend.auth.entity.UserEntity;
import com.xora.backend.auth.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE u.status = :status
            AND (
                LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """)
    List<UserEntity> searchActiveUsers(
            @Param("status") UserStatus status,
            @Param("query") String query);
}
