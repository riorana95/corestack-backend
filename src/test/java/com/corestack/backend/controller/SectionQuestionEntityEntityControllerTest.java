package com.corestack.backend.controller;

import com.corestack.backend.dto.SectionQuestionRequestDTO;
import com.corestack.backend.enums.SectionType;
import com.corestack.backend.service.SectionQuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SectionQuestionController.class)
class SectionQuestionEntityEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SectionQuestionService sectionQuestionService;

    @Test
    void shouldReturnQuestionsForRequestedSection() throws Exception {
        SectionQuestionRequestDTO response = new SectionQuestionRequestDTO(
                1L,
                "frontend",
                "What is REST?",
                "REST is an architectural style.",
                "Stateless API style.",
                "beginner",
                "mixed",
                Map.of("text", "REST explanation"),
                List.of("api")
        );

        when(sectionQuestionService.getQuestionsBySection(SectionType.FRONTEND))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/questions/section/frontend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("What is REST?"))
                .andExpect(jsonPath("$[0].content.text").value("REST explanation"))
                .andExpect(jsonPath("$[0].tags[0]").value("api"));
    }

    @Test
    void shouldRejectUnsupportedSectionType() throws Exception {
        mockMvc.perform(get("/questions/section/mobile"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Unsupported section type 'mobile'. Supported values: frontend, backend"));

        verifyNoInteractions(sectionQuestionService);
    }
}
