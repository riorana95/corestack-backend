package com.xora.backend.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for creating a company (a company + role + round + date combo).
 */
public record CompanyRequestDTO(
        Long id,

        @NotBlank(message = "Company name is required")
        @Size(max = 200, message = "Company name must be at most 200 characters")
        String name,

        @NotBlank(message = "Role is required")
        @Size(max = 200, message = "Role must be at most 200 characters")
        String role,

        @Size(max = 100, message = "Round must be at most 100 characters")
        String round,

        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate date
) {
}
