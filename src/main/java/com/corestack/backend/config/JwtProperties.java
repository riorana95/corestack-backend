package com.corestack.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @DefaultValue("change-me-use-a-long-random-secret-at-least-32-chars") String secret,
        @DefaultValue("900000") long accessTokenExpirationMs,
        @DefaultValue("604800000") long refreshTokenExpirationMs
) {
}
