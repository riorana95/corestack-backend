package com.corestack.backend.repository;

import com.corestack.backend.entity.SectionQuestionEntity;
import com.corestack.backend.enums.SectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionQuestionRepository extends JpaRepository<SectionQuestionEntity, Long> {
    List<SectionQuestionEntity> findBySectionOrderByIdAsc(SectionType section);
}
