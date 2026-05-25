package com.corestack.backend.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtil {

    public static final int MONEY_SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private MoneyUtil() {
    }

    public static BigDecimal normalize(BigDecimal value) {
        return value.setScale(MONEY_SCALE, ROUNDING);
    }
}
