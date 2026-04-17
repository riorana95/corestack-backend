package com.corestack.backend.service;

import com.corestack.backend.dto.SectionQuestionRequestDTO;
import com.corestack.backend.entity.SectionQuestionEntity;
import com.corestack.backend.enums.SectionType;

import java.util.List;

public interface SectionQuestionService {
    List<SectionQuestionRequestDTO> getQuestionsBySection(SectionType sectionType);
    List<SectionQuestionEntity> createSection(List<SectionQuestionRequestDTO> requests);
}
