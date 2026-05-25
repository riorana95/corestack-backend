package com.corestack.backend.auth.service;

import com.corestack.backend.auth.dto.AuthResponse;
import com.corestack.backend.auth.dto.LoginRequest;
import com.corestack.backend.auth.dto.RefreshTokenRequest;
import com.corestack.backend.auth.dto.RegisterRequest;
import com.corestack.backend.auth.dto.UserResponse;
import com.corestack.backend.auth.entity.UserAuthProviderEntity;
import com.corestack.backend.auth.entity.UserEntity;
import com.corestack.backend.auth.enums.AuthProviderType;
import com.corestack.backend.auth.enums.UserStatus;
import com.corestack.backend.auth.mapper.AuthMapper;
import com.corestack.backend.auth.repository.UserAuthProviderRepository;
import com.corestack.backend.auth.repository.UserRepository;
import com.corestack.backend.common.exception.BusinessException;
import com.corestack.backend.common.exception.ErrorCode;
import com.corestack.backend.config.JwtProperties;
import com.corestack.backend.security.UserPrincipal;
import com.corestack.backend.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final AuthMapper authMapper;

    public AuthService(UserRepository userRepository,
                       UserAuthProviderRepository userAuthProviderRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       JwtProperties jwtProperties,
                       AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.authMapper = authMapper;
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

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN,
                    "Account is disabled",
                    HttpStatus.FORBIDDEN);
        }

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
