package com.xora.backend.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request payload for creating or updating an interview question.
 * All fields are validated at the controller boundary via {@code @Valid}.
 */
public record QuestionRequestDTO(
        Long id,

        @NotBlank(message = "Question text is required")
        @Size(max = 1000, message = "Question text must be at most 1000 characters")
        String question,

        @Size(max = 10000, message = "Description must be at most 10000 characters")
        String description,

        @NotBlank(message = "Difficulty is required")
        @Pattern(regexp = "beginner|intermediate|advanced",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Difficulty must be beginner, intermediate, or advanced")
        String difficulty,

        @Pattern(regexp = "mixed|text|code",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Content type must be mixed, text, or code")
        String contentType,

        @Size(max = 50000, message = "Content must be at most 50000 characters")
        String content,

        @Size(max = 20, message = "At most 20 tags are allowed")
        List<@NotBlank(message = "Tag cannot be blank") String> tags,

        @NotNull(message = "companyId is required")
        Long companyId
) {
}
