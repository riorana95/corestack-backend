package com.xora.backend.interview.repository;

import com.xora.backend.interview.entity.QuestionEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

       List<QuestionEntity> findByCompanies_Id(Long companyId);

       @Query("""
                     SELECT DISTINCT q FROM QuestionEntity q
                     LEFT JOIN q.tags t
                     LEFT JOIN q.companies c
                     WHERE (:companyName IS NULL OR :companyName = ''
                            OR LOWER(c.name) = LOWER(:companyName))
                     AND (:tag IS NULL OR :tag = ''
                            OR LOWER(t) = LOWER(:tag))
                     """)
       Page<QuestionEntity> findFilteredQuestions(
                     @Param("companyName") String companyName,
                     @Param("tag") String tag,
                     Pageable pageable);
}
