package com.corestack.backend.security.jwt;

import com.corestack.backend.config.JwtProperties;
import com.corestack.backend.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    private static final String FALLBACK_DEV_SECRET =
            "change-me-use-a-long-random-secret-at-least-32-chars";

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        String secret = resolveSecret(jwtProperties.secret());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private static String resolveSecret(String configured) {
        if (configured == null || configured.isBlank()) {
            return FALLBACK_DEV_SECRET;
        }
        if (configured.length() < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least 32 characters (set in application-local.properties)");
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
