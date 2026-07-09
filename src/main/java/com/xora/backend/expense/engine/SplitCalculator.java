package com.xora.backend.expense.engine;

import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.common.util.MoneyUtil;
import com.xora.backend.expense.enums.SplitType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class SplitCalculator {

    public List<SplitLine> calculate(
            SplitType splitType,
            BigDecimal total,
            List<UUID> participantUserIds,
            List<SplitLine> inputLines) {
        return switch (splitType) {
            case EQUAL -> calculateEqual(total, participantUserIds);
            case EXACT -> calculateExact(total, inputLines);
            case PERCENTAGE -> calculatePercentage(total, inputLines);
        };
    }

    public List<SplitLine> calculateEqual(BigDecimal total, List<UUID> participantUserIds) {
        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "At least one participant is required for equal split",
                    HttpStatus.BAD_REQUEST);
        }

        List<UUID> sorted = participantUserIds.stream().distinct().sorted().toList();
        int count = sorted.size();

        long totalMinor = toMinorUnits(total);
        long baseMinor = totalMinor / count;
        long remainderMinor = totalMinor % count;

        List<SplitLine> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            long shareMinor = baseMinor + (i < remainderMinor ? 1 : 0);
            lines.add(new SplitLine(sorted.get(i), fromMinorUnits(shareMinor), null));
        }
        return lines;
    }

    public List<SplitLine> calculateExact(BigDecimal total, List<SplitLine> inputLines) {
        validateInputLines(inputLines);
        BigDecimal sum = inputLines.stream()
                .map(SplitLine::shareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertTotalMatches(total, sum);
        return inputLines.stream()
                .map(line -> new SplitLine(
                        line.userId(),
                        MoneyUtil.normalize(line.shareAmount()),
                        null))
                .toList();
    }

    public List<SplitLine> calculatePercentage(BigDecimal total, List<SplitLine> inputLines) {
        validateInputLines(inputLines);
        BigDecimal percentSum = inputLines.stream()
                .map(line -> line.sharePercent().setScale(MoneyUtil.MONEY_SCALE, MoneyUtil.ROUNDING))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (percentSum.compareTo(new BigDecimal("100.0000")) != 0) {
            throw new BusinessException(
                    ErrorCode.SPLIT_TOTAL_MISMATCH,
                    "Percentages must sum to 100",
                    HttpStatus.BAD_REQUEST);
        }

        List<SplitLine> sortedByUser = inputLines.stream()
                .sorted(Comparator.comparing(SplitLine::userId))
                .toList();

        long totalMinor = toMinorUnits(total);
        long allocatedMinor = 0;
        List<SplitLine> result = new ArrayList<>();

        for (int i = 0; i < sortedByUser.size(); i++) {
            SplitLine line = sortedByUser.get(i);
            long shareMinor;
            if (i == sortedByUser.size() - 1) {
                shareMinor = totalMinor - allocatedMinor;
            } else {
                shareMinor = toMinorUnits(
                        total.multiply(line.sharePercent())
                                .divide(new BigDecimal("100"), MoneyUtil.MONEY_SCALE, MoneyUtil.ROUNDING));
                allocatedMinor += shareMinor;
            }
            result.add(new SplitLine(line.userId(), fromMinorUnits(shareMinor), line.sharePercent()));
        }

        BigDecimal sum = result.stream().map(SplitLine::shareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertTotalMatches(total, sum);
        return result;
    }

    private void validateInputLines(List<SplitLine> inputLines) {
        if (inputLines == null || inputLines.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "Split lines are required",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void assertTotalMatches(BigDecimal total, BigDecimal sum) {
        if (MoneyUtil.normalize(total).compareTo(MoneyUtil.normalize(sum)) != 0) {
            throw new BusinessException(
                    ErrorCode.SPLIT_TOTAL_MISMATCH,
                    "Split amounts must sum to expense total",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private long toMinorUnits(BigDecimal amount) {
        return MoneyUtil.normalize(amount).movePointRight(MoneyUtil.MONEY_SCALE).longValueExact();
    }

    private BigDecimal fromMinorUnits(long minor) {
        return BigDecimal.valueOf(minor, MoneyUtil.MONEY_SCALE);
    }
}
