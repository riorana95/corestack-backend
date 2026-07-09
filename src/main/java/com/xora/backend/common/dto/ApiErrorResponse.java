package com.xora.backend.common.dto;

import com.xora.backend.common.exception.ErrorCode;

import java.time.Instant;

/**
 * Standard error envelope returned by every failed API call.
 * Shape is intentionally stable so the Angular frontend can switch on
 * {@link #code()} for toast routing.
 *
 * @param timestamp when the error happened (server clock)
 * @param status    HTTP status code (mirror of the response line)
 * @param code      stable domain error code
 * @param message   user-safe message (never leak stack traces / SQL)
 * @param path      request path that triggered the error
 * @param requestId optional correlation id (X-Request-Id header) — populated by RequestIdFilter
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        ErrorCode code,
        String message,
        String path,
        String requestId
) {
    public ApiErrorResponse(Instant timestamp, int status, ErrorCode code, String message, String path) {
        this(timestamp, status, code, message, path, null);
    }
}
