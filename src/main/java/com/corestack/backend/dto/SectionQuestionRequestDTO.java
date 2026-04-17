package com.corestack.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionQuestionRequestDTO {
    private Long id;
    private String section;
    private String question;
    private String answer;
    private String description;
    private String difficulty;
    private String contentType;
    private Map<String, Object> content;
    private List<String> tags;
}
