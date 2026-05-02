package com.corestack.backend.repository;

import com.corestack.backend.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByCompanyEntity_Id(Long companyId);
    // filter will add it
    // List<QuestionEntity> findAllQuestion();
    @Query("SELECT q.question,q.companyEntity FROM QuestionEntity q")
    List<QuestionEntity> findFilterQuestion();
}
