package com.xora.backend.security.jwt;

import com.xora.backend.config.JwtProperties;
import com.xora.backend.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Issues and validates HS256 JWTs. The signing secret MUST be supplied via
 * {@code app.jwt.secret} — there is NO silent fallback to a dev secret.
 * Startup will fail fast if the secret is missing or too short.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private static final int MIN_SECRET_LENGTH = 32;

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(resolveSecret(jwtProperties.secret()).getBytes(StandardCharsets.UTF_8));
    }

    private static String resolveSecret(String configured) {
        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException(
                    "app.jwt.secret is not configured. Set it in application-local.properties "
                            + "or via the XORA_JWT_SECRET environment variable. Startup aborted.");
        }
        if (configured.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least " + MIN_SECRET_LENGTH
                            + " characters. Current length: " + configured.length());
        }
        return configured;
    }

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(principal, jwtProperties.accessTokenExpirationMs(), TYPE_ACCESS);
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return buildToken(principal, jwtProperties.refreshTokenExpirationMs(), TYPE_REFRESH);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public UUID getUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(parseClaims(token).get(CLAIM_TYPE, String.class));
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(parseClaims(token).get(CLAIM_TYPE, String.class));
    }

    private String buildToken(UserPrincipal principal, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(principal.getId().toString())
                .claim(CLAIM_EMAIL, principal.getEmail())
                .claim(CLAIM_TYPE, tokenType)
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
