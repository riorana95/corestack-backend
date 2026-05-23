package com.corestack.backend.interview.service;

import com.corestack.backend.interview.dto.PageResponseDTO;
import com.corestack.backend.interview.dto.QuestionResponseDTO;
import com.corestack.backend.interview.entity.QuestionEntity;

import java.util.List;

public interface QuestionService {
    List<QuestionEntity> getQuestionsByCompanyId(Long companyId);

    QuestionEntity createQuestion(QuestionEntity questionEntity);

    List<QuestionEntity> createQuestions(List<QuestionEntity> questionEntities);

    QuestionEntity updateQuestion(Long id, QuestionEntity questionEntity);

    PageResponseDTO<QuestionResponseDTO> getFilteredQuestions(String companyName, String tag, int page, int size);
}
