package com.corestack.backend.service;

import com.corestack.backend.dto.QuestionResponseDTO;
import com.corestack.backend.entity.QuestionEntity;

import java.util.List;

public interface QuestionService {
    List<QuestionEntity> getQuestionsByCompanyId(Long companyId);

    QuestionEntity createQuestion(QuestionEntity questionEntity);

    List<QuestionEntity> createQuestions(List<QuestionEntity> questionEntities);

    QuestionEntity updateQuestion(Long id, QuestionEntity questionEntity);

    List<QuestionResponseDTO> getAllQuestion();
}
