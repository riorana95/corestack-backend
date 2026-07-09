package com.xora.backend.expense.engine;

import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.expense.enums.SplitType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitCalculatorTest {

    private final SplitCalculator calculator = new SplitCalculator();

    @Test
    void equalSplitDistributesRemainderDeterministically() {
        UUID u1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID u2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID u3 = UUID.fromString("00000000-0000-0000-0000-000000000003");

        List<SplitLine> lines = calculator.calculateEqual(new BigDecimal("100.0000"), List.of(u3, u1, u2));
        BigDecimal sum = lines.stream().map(SplitLine::shareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("100.0000"), sum);
        assertEquals(3, lines.size());
    }

    @Test
    void exactSplitRejectsMismatch() {
        UUID u1 = UUID.randomUUID();
        List<SplitLine> input = List.of(new SplitLine(u1, new BigDecimal("40.0000"), null));

        assertThrows(BusinessException.class, () ->
                calculator.calculate(SplitType.EXACT, new BigDecimal("100.0000"), List.of(), input));
    }

    @Test
    void percentageSplitRequiresHundredPercent() {
        UUID u1 = UUID.randomUUID();
        List<SplitLine> input = List.of(new SplitLine(u1, null, new BigDecimal("50.0000")));

        assertThrows(BusinessException.class, () ->
                calculator.calculate(SplitType.PERCENTAGE, new BigDecimal("100.0000"), List.of(), input));
    }
}
