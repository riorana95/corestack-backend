package com.xora.backend.settlement.mapper;

import com.xora.backend.settlement.dto.SettlementResponse;
import com.xora.backend.settlement.entity.SettlementEntity;
import org.springframework.stereotype.Component;

@Component
public class SettlementMapper {

    public SettlementResponse toResponse(SettlementEntity settlement) {
        return new SettlementResponse(
                settlement.getId(),
                settlement.getGroup().getId(),
                settlement.getFromUser().getId(),
                settlement.getFromUser().getDisplayName(),
                settlement.getToUser().getId(),
                settlement.getToUser().getDisplayName(),
                settlement.getAmount(),
                settlement.getStatus(),
                settlement.getNote(),
                settlement.getSettledAt(),
                settlement.getCreatedAt());
    }
}
