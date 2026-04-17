package com.corestack.backend.service.impl;

import com.corestack.backend.dto.SectionQuestionRequestDTO;
import com.corestack.backend.entity.SectionQuestionEntity;
import com.corestack.backend.enums.SectionType;
import com.corestack.backend.repository.SectionQuestionRepository;
import com.corestack.backend.service.SectionQuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SectionQuestionServiceImpl implements SectionQuestionService {

    private final SectionQuestionRepository sectionQuestionRepository;

    public SectionQuestionServiceImpl(SectionQuestionRepository sectionQuestionRepository) {
        this.sectionQuestionRepository = sectionQuestionRepository;
    }

    @Override
    public List<SectionQuestionRequestDTO> getQuestionsBySection(SectionType sectionType) {
        return sectionQuestionRepository.findBySectionOrderByIdAsc(sectionType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<SectionQuestionEntity> createSection(List<SectionQuestionRequestDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must contain at least one question.");
        }

        List<SectionQuestionEntity> entities = requests.stream()
                .map(this::toEntity)
                .toList();

        return sectionQuestionRepository.saveAll(entities);
    }

    private SectionQuestionRequestDTO toResponse(SectionQuestionEntity question) {
        return new SectionQuestionRequestDTO(
                question.getId(),
                question.getSection().getValue(),
                question.getQuestion(),
                question.getAnswer(),
                question.getDescription(),
                question.getDifficulty(),
                question.getContentType(),
                question.getContent(),
                question.getTags()
        );
    }

    private SectionQuestionEntity toEntity(SectionQuestionRequestDTO request) {
        if (request.getSection() == null || request.getSection().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section is required.");
        }
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question is required.");
        }
        if (request.getAnswer() == null || request.getAnswer().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer is required.");
        }

        SectionQuestionEntity questionEntity = new SectionQuestionEntity();
        questionEntity.setSection(SectionType.fromValue(request.getSection()));
        questionEntity.setQuestion(request.getQuestion());
        questionEntity.setAnswer(request.getAnswer());
        questionEntity.setDescription(request.getDescription());
        questionEntity.setDifficulty(request.getDifficulty());
        questionEntity.setContentType(request.getContentType());
        questionEntity.setContent(request.getContent());
        questionEntity.setTags(request.getTags());
        return questionEntity;
    }
}
