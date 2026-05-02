package com.corestack.backend.dto;

public record QuestionSummaryRowDTO(
        Long id,
        String question,
        String companyName,
        String companyRole,
        String tag
) {
}
