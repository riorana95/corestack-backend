package com.xora.backend.auth.service;

import com.xora.backend.auth.dto.AuthResponse;
import com.xora.backend.auth.dto.GoogleLoginRequest;
import com.xora.backend.auth.dto.GoogleTokenInfoResponse;
import com.xora.backend.auth.dto.LoginRequest;
import com.xora.backend.auth.dto.RefreshTokenRequest;
import com.xora.backend.auth.dto.RegisterRequest;
import com.xora.backend.auth.dto.UserResponse;
import com.xora.backend.auth.entity.UserAuthProviderEntity;
import com.xora.backend.auth.entity.UserEntity;
import com.xora.backend.auth.enums.AuthProviderType;
import com.xora.backend.auth.enums.UserStatus;
import com.xora.backend.auth.mapper.AuthMapper;
import com.xora.backend.auth.repository.UserAuthProviderRepository;
import com.xora.backend.auth.repository.UserRepository;
import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.config.GoogleOAuthProperties;
import com.xora.backend.config.JwtProperties;
import com.xora.backend.security.UserPrincipal;
import com.xora.backend.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class AuthService {

    private static final String GOOGLE_TOKEN_INFO_URL =
            "https://oauth2.googleapis.com/tokeninfo?id_token={credential}";

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final GoogleOAuthProperties googleOAuthProperties;
    private final AuthMapper authMapper;
    private final RestClient restClient;

    public AuthService(UserRepository userRepository,
                       UserAuthProviderRepository userAuthProviderRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       JwtProperties jwtProperties,
                       GoogleOAuthProperties googleOAuthProperties,
                       AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.googleOAuthProperties = googleOAuthProperties;
        this.authMapper = authMapper;
        this.restClient = RestClient.create();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "Email is already registered",
                    HttpStatus.CONFLICT);
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email().trim().toLowerCase());
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        UserEntity savedUser = userRepository.save(user);

        UserAuthProviderEntity provider = new UserAuthProviderEntity();
        provider.setUser(savedUser);
        provider.setProvider(AuthProviderType.LOCAL);
        provider.setProviderUserId(savedUser.getEmail());
        userAuthProviderRepository.save(provider);

        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        ensureActive(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        GoogleTokenInfoResponse tokenInfo = verifyGoogleCredential(request.credential());

        UserAuthProviderEntity existingProvider = userAuthProviderRepository
                .findByProviderAndProviderUserId(AuthProviderType.GOOGLE, tokenInfo.sub())
                .orElse(null);

        if (existingProvider != null) {
            ensureActive(existingProvider.getUser());
            return buildAuthResponse(existingProvider.getUser());
        }

        UserEntity user = userRepository.findByEmailIgnoreCase(tokenInfo.email())
                .orElseGet(() -> createGoogleUser(tokenInfo));
        ensureActive(user);

        UserAuthProviderEntity provider = new UserAuthProviderEntity();
        provider.setUser(user);
        provider.setProvider(AuthProviderType.GOOGLE);
        provider.setProviderUserId(tokenInfo.sub());
        userAuthProviderRepository.save(provider);

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();
        if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isRefreshToken(token)) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    "Invalid refresh token",
                    HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = userRepository.findById(jwtTokenProvider.getUserId(token))
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.UNAUTHORIZED,
                        "User not found",
                        HttpStatus.UNAUTHORIZED));

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        UserEntity user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "User not found",
                        HttpStatus.NOT_FOUND));
        return authMapper.toUserResponse(user);
    }

    private GoogleTokenInfoResponse verifyGoogleCredential(String credential) {
        if (!StringUtils.hasText(googleOAuthProperties.clientId())) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    "Google OAuth client id is not configured",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        GoogleTokenInfoResponse tokenInfo = restClient
                .get()
                .uri(GOOGLE_TOKEN_INFO_URL, credential)
                .retrieve()
                .body(GoogleTokenInfoResponse.class);

        if (tokenInfo == null
                || !Objects.equals(tokenInfo.aud(), googleOAuthProperties.clientId())
                || !Boolean.TRUE.equals(tokenInfo.emailVerified())
                || !StringUtils.hasText(tokenInfo.sub())
                || !StringUtils.hasText(tokenInfo.email())) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    "Invalid Google credential",
                    HttpStatus.UNAUTHORIZED);
        }

        return tokenInfo;
    }

    private UserEntity createGoogleUser(GoogleTokenInfoResponse tokenInfo) {
        UserEntity user = new UserEntity();
        user.setEmail(tokenInfo.email().trim().toLowerCase());
        user.setDisplayName(resolveGoogleDisplayName(tokenInfo));
        user.setPasswordHash(null);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private String resolveGoogleDisplayName(GoogleTokenInfoResponse tokenInfo) {
        if (StringUtils.hasText(tokenInfo.name())) {
            return tokenInfo.name().trim();
        }
        return tokenInfo.email().split("@")[0];
    }

    private void ensureActive(UserEntity user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Account is disabled",
                    HttpStatus.FORBIDDEN);
        }
    }

    private AuthResponse buildAuthResponse(UserEntity user) {
        UserPrincipal principal = new UserPrincipal(user);
        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(principal),
                jwtTokenProvider.generateRefreshToken(principal),
                "Bearer",
                jwtProperties.accessTokenExpirationMs(),
                authMapper.toUserResponse(user));
    }
}
