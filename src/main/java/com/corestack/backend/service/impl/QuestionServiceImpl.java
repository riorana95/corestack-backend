package com.corestack.backend.service.impl;

import com.corestack.backend.entity.QuestionEntity;
import com.corestack.backend.repository.QuestionRepository;
import com.corestack.backend.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionEntity> getQuestionsByCompanyId(Long companyId) {
        return questionRepository.findByCompanyEntity_Id(companyId);
    }

    @Override
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionRepository.save(questionEntity);
    }

    @Override
    public QuestionEntity updateQuestion(Long id, QuestionEntity questionEntity) {
        QuestionEntity existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Question not found with id " + id
                ));

        existingQuestion.setQuestion(questionEntity.getQuestion());
        existingQuestion.setDescription(questionEntity.getDescription());
        existingQuestion.setDifficulty(questionEntity.getDifficulty());
        existingQuestion.setContentType(questionEntity.getContentType());
        existingQuestion.setContent(questionEntity.getContent());
        existingQuestion.setTags(questionEntity.getTags());
        existingQuestion.setCompanyEntity(questionEntity.getCompanyEntity());

        return questionRepository.save(existingQuestion);
    }

    @Override
    public List<QuestionEntity> createQuestions(List<QuestionEntity> questionEntities) {
        return questionRepository.saveAll(questionEntities);
    }
}
