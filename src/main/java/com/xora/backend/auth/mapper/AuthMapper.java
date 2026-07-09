package com.xora.backend.auth.mapper;

import com.xora.backend.auth.dto.UserResponse;
import com.xora.backend.auth.entity.UserEntity;
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
