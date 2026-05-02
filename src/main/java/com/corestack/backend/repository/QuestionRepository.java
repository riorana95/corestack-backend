package com.corestack.backend.repository;

import com.corestack.backend.dto.QuestionResponseDTO;
import com.corestack.backend.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByCompanyEntity_Id(Long companyId);

    @Query("""
            SELECT new com.corestack.backend.dto.QuestionResponseDTO(
                q.id,
                q.question,
                q.companyEntity.name,
                q.companyEntity.role
            )
            FROM QuestionEntity q
            """)
    List<QuestionResponseDTO> findAllSummaries();
}
