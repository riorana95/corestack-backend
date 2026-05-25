package com.corestack.backend.auth.mapper;

import com.corestack.backend.auth.dto.UserResponse;
import com.corestack.backend.auth.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getStatus());
    }
}
