package com.corestack.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionRequestDTO {
    private Long id;

    private String question;
    private String description;

    private String difficulty;

    private String contentType;

    private String content;

    private List<String> tags;

    private Long companyId;
}
