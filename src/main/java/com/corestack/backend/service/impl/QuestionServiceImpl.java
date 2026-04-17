package com.corestack.backend.service.impl;

import com.corestack.backend.entity.QuestionEntity;
import com.corestack.backend.repository.QuestionRepository;
import com.corestack.backend.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionEntity> getQuestionsByCompanyId(Long companyId) {
        return questionRepository.findByCompanyId(companyId);
    }

    @Override
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionRepository.save(questionEntity);
    }

    @Override
    public List<QuestionEntity> createQuestions(List<QuestionEntity> questionEntities) {
        return questionRepository.saveAll(questionEntities);
    }
}
