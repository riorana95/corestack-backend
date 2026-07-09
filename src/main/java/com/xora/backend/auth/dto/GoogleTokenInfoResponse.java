package com.xora.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenInfoResponse(
        String sub,
        String aud,
        String email,
        String name,
        String picture,
        @JsonProperty("email_verified") Boolean emailVerified
) {
}
