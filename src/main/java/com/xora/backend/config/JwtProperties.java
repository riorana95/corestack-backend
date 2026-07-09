package com.xora.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * JWT configuration. The {@code secret} MUST be supplied via the
 * {@code app.jwt.secret} property — there is NO silent fallback. If it is
 * missing or shorter than 32 characters, application startup will fail
 * (see {@link com.xora.backend.security.jwt.JwtTokenProvider}).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @DefaultValue String secret,
        @DefaultValue("900000") long accessTokenExpirationMs,
        @DefaultValue("604800000") long refreshTokenExpirationMs,
        @DefaultValue("xora") String issuer
) {
}
