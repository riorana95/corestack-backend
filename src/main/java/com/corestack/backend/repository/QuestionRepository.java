package com.corestack.backend.repository;
import com.corestack.backend.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByCompanyEntity_Id(Long companyId);
    @Query("""
    SELECT DISTINCT q FROM QuestionEntity q
    LEFT JOIN q.tags t
    WHERE (:companyName IS NULL OR :companyName = '' 
           OR LOWER(q.companyEntity.name) = LOWER(:companyName))
    AND (:tag IS NULL OR :tag = '' 
           OR LOWER(t) = LOWER(:tag))
    """)
    Page<QuestionEntity> findFilteredQuestions(
            @Param("companyName") String companyName,
            @Param("tag") String tag,
            Pageable pageable
    );
}
