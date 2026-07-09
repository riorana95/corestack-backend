package com.xora.backend.interview.mapper;

import com.xora.backend.interview.dto.CompanyRequestDTO;
import com.xora.backend.interview.dto.CompanyResponseDTO;
import com.xora.backend.interview.entity.CompanyEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link CompanyEntity} and the company DTOs.
 * Kept hand-written (no MapStruct) to keep the build dependency-free.
 */
@Component
public class CompanyMapper {

    public CompanyEntity toEntity(CompanyRequestDTO request) {
        CompanyEntity entity = new CompanyEntity();
        entity.setId(request.id());
        entity.setName(request.name());
        entity.setRole(request.role());
        entity.setRound(request.round());
        entity.setDate(request.date());
        return entity;
    }

    public CompanyResponseDTO toResponse(CompanyEntity entity) {
        return new CompanyResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getRole(),
                entity.getRound(),
                entity.getDate());
    }
}
