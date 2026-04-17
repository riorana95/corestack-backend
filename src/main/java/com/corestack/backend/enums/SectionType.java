package com.corestack.backend.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

public enum SectionType {
    FRONTEND("frontend"),
    BACKEND("backend");

    private final String value;

    SectionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static SectionType fromValue(String value) {
        return Arrays.stream(values())
                .filter(sectionType -> sectionType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unsupported section type '" + value + "'. Supported values: frontend, backend"
                ));
    }
}
