package com.corestack.backend.repository;

import com.corestack.backend.dto.QuestionSummaryRowDTO;
import com.corestack.backend.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByCompanyEntity_Id(Long companyId);

    @Query("""
            SELECT new com.corestack.backend.dto.QuestionSummaryRowDTO(
                q.id,
                q.question,
                q.companyEntity.name,
                q.companyEntity.role,
                tag
            )
            FROM QuestionEntity q
            LEFT JOIN q.tags tag
            ORDER BY q.id
            """)
    List<QuestionSummaryRowDTO> findAllSummaryRows();
}
