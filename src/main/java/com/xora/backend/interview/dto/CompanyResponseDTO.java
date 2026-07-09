package com.xora.backend.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Full company response (lighter than the entity — does not leak the questions back-reference).
 */
public record CompanyResponseDTO(
        Long id,
        String name,
        String role,
        String round,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate date
) {
}
