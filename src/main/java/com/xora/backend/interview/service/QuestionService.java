package com.xora.backend.interview.service;

import com.xora.backend.interview.dto.PageResponseDTO;
import com.xora.backend.interview.dto.QuestionRequestDTO;
import com.xora.backend.interview.dto.QuestionResponseDTO;

import java.util.List;

/**
 * Interview-prep question service. All methods return DTOs (never raw entities)
 * and all writes are wrapped in {@code @Transactional}.
 */
public interface QuestionService {

    PageResponseDTO<QuestionResponseDTO> getFilteredQuestions(String companyName, String tag, int page, int size);

    List<QuestionResponseDTO> getQuestionsByCompanyId(Long companyId);

    QuestionResponseDTO createQuestion(QuestionRequestDTO request);

    QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO request);

    List<QuestionResponseDTO> createQuestions(List<QuestionRequestDTO> requests);
}
