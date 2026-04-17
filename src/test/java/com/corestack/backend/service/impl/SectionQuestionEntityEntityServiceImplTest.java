package com.corestack.backend.service.impl;

import com.corestack.backend.dto.SectionQuestionRequestDTO;
import com.corestack.backend.entity.SectionQuestionEntity;
import com.corestack.backend.enums.SectionType;
import com.corestack.backend.repository.SectionQuestionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SectionQuestionEntityEntityServiceImplTest {

    private final SectionQuestionRepository sectionQuestionRepository = mock(SectionQuestionRepository.class);
    private final SectionQuestionServiceImpl sectionQuestionService =
            new SectionQuestionServiceImpl(sectionQuestionRepository);

    @Test
    void shouldReturnMappedQuestionResponsesForRequestedSection() {
        SectionQuestionEntity question = new SectionQuestionEntity();
        question.setId(10L);
        question.setSection(SectionType.FRONTEND);
        question.setQuestion("What is the virtual DOM?");
        question.setAnswer("An in-memory representation of the DOM.");
        question.setDescription("A lightweight DOM representation.");
        question.setDifficulty("beginner");
        question.setContentType("mixed");
        question.setContent(Map.of("text", "Sample"));
        question.setTags(List.of("react"));

        when(sectionQuestionRepository.findBySectionOrderByIdAsc(SectionType.FRONTEND))
                .thenReturn(List.of(question));

        List<SectionQuestionRequestDTO> result = sectionQuestionService.getQuestionsBySection(SectionType.FRONTEND);

        assertThat(result).hasSize(1);
        SectionQuestionRequestDTO response = result.getFirst();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getSection()).isEqualTo("frontend");
        assertThat(response.getQuestion()).isEqualTo("What is the virtual DOM?");
        assertThat(response.getAnswer()).isEqualTo("An in-memory representation of the DOM.");
        assertThat(response.getContent()).containsEntry("text", "Sample");
        assertThat(response.getTags()).containsExactly("react");
    }
}
