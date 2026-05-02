package com.corestack.backend.dto;

public record QuestionResponseDTO(
        Long id,
        String question,
        String companyName,
        String companyRole
) {
}
