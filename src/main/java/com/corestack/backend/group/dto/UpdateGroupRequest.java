package com.corestack.backend.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateGroupRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 500) String description,
        @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a 3-letter ISO code") String currencyCode
) {
}
