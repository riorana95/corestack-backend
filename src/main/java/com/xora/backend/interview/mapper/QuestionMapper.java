package com.xora.backend.interview.mapper;

import com.xora.backend.interview.dto.CompanyDTO;
import com.xora.backend.interview.dto.QuestionRequestDTO;
import com.xora.backend.interview.dto.QuestionResponseDTO;
import com.xora.backend.interview.entity.CompanyEntity;
import com.xora.backend.interview.entity.QuestionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps between {@link QuestionEntity} and the question DTOs.
 */
@Component
public class QuestionMapper {

    public QuestionEntity toEntity(QuestionRequestDTO request, CompanyEntity company) {
        QuestionEntity entity = new QuestionEntity();
        entity.setQuestion(request.question());
        entity.setDescription(request.description());
        entity.setDifficulty(request.difficulty());
        entity.setContentType(request.contentType());
        entity.setContent(request.content());
        entity.setTags(request.tags());
        if (company != null) {
            entity.getCompanies().add(company);
        }
        return entity;
    }

    public QuestionResponseDTO toResponse(QuestionEntity entity) {
        List<CompanyDTO> companies = entity.getCompanies() == null
                ? List.of()
                : entity.getCompanies().stream()
                        .map(c -> new CompanyDTO(c.getId(), c.getName(), c.getRole()))
                        .toList();
        return new QuestionResponseDTO(
                entity.getId(),
                entity.getQuestion(),
                entity.getDescription(),
                companies,
                entity.getTags());
    }

    public List<QuestionResponseDTO> toResponseList(List<QuestionEntity> entities) {
        return entities.stream().map(this::toResponse).toList();
    }
}
