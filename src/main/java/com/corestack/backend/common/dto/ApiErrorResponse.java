package com.corestack.backend.common.dto;

import com.corestack.backend.common.exception.ErrorCode;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        ErrorCode code,
        String message,
        String path
) {
}
