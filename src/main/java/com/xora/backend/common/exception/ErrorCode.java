package com.xora.backend.common.exception;

/**
 * Stable, machine-readable error codes surfaced in
 * {@link com.xora.backend.common.dto.ApiErrorResponse#code()}.
 * Frontend toasts can switch on these to decide whether to retry, redirect, or just inform.
 */
public enum ErrorCode {
    VALIDATION_ERROR,
    RESOURCE_NOT_FOUND,
    UNAUTHORIZED,
    FORBIDDEN,
    CONFLICT,
    INTERNAL_ERROR,
    INVALID_CREDENTIALS,
    SPLIT_TOTAL_MISMATCH,
    BAD_REQUEST,
    METHOD_NOT_ALLOWED,
    NOT_READABLE,
    ACCESS_DENIED
}
