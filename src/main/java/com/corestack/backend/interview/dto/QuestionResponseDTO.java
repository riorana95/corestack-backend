package com.corestack.backend.interview.dto;

import java.util.List;

public record QuestionResponseDTO(
                Long id,
                String question,
                String description,
                List<CompanyDTO> companies,
                List<String> tags) {
}
