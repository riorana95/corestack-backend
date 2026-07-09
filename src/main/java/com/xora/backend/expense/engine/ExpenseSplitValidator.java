package com.xora.backend.expense.engine;

import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.expense.enums.SplitType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class ExpenseSplitValidator {

    public void validateSplitRequest(
            SplitType splitType,
            List<UUID> participantUserIds,
            List<SplitLine> inputLines) {
        switch (splitType) {
            case EQUAL -> validateEqualParticipants(participantUserIds);
            case EXACT -> validateExactLines(inputLines);
            case PERCENTAGE -> validatePercentageLines(inputLines);
        }
    }

    public void validateParticipantsAreMembers(Set<UUID> activeMemberIds, List<UUID> participantIds) {
        for (UUID participantId : participantIds) {
            if (!activeMemberIds.contains(participantId)) {
                throw new BusinessException(
                        ErrorCode.FORBIDDEN,
                        "Participant is not an active group member",
                        HttpStatus.FORBIDDEN);
            }
        }
    }

    private void validateEqualParticipants(List<UUID> participantUserIds) {
        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "participantUserIds is required for equal split",
                    HttpStatus.BAD_REQUEST);
        }
        if (participantUserIds.size() != new HashSet<>(participantUserIds).size()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "Duplicate participants are not allowed",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateExactLines(List<SplitLine> inputLines) {
        if (inputLines == null || inputLines.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "splits are required for exact split",
                    HttpStatus.BAD_REQUEST);
        }
        Set<UUID> seen = new HashSet<>();
        for (SplitLine line : inputLines) {
            if (line.shareAmount() == null || line.shareAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_ERROR,
                        "Each split must have a non-negative shareAmount",
                        HttpStatus.BAD_REQUEST);
            }
            if (!seen.add(line.userId())) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_ERROR,
                        "Duplicate user in splits",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void validatePercentageLines(List<SplitLine> inputLines) {
        if (inputLines == null || inputLines.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "splits are required for percentage split",
                    HttpStatus.BAD_REQUEST);
        }
        Set<UUID> seen = new HashSet<>();
        for (SplitLine line : inputLines) {
            if (line.sharePercent() == null || line.sharePercent().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_ERROR,
                        "Each split must have a positive sharePercent",
                        HttpStatus.BAD_REQUEST);
            }
            if (!seen.add(line.userId())) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_ERROR,
                        "Duplicate user in splits",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }
}
