package com.corestack.backend.expense.dto;

import java.util.List;
import java.util.UUID;

public record GroupBalancesResponse(
        UUID groupId,
        String currencyCode,
        List<MemberBalanceResponse> balances
) {
}
