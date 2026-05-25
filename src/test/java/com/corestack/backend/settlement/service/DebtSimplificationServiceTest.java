package com.corestack.backend.settlement.service;

import com.corestack.backend.expense.dto.MemberBalanceResponse;
import com.corestack.backend.settlement.dto.SimplifiedDebtResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DebtSimplificationServiceTest {

    private final DebtSimplificationService service = new DebtSimplificationService(null);

    @Test
    void simplifiesTwoPersonDebt() {
        UUID alice = UUID.randomUUID();
        UUID bob = UUID.randomUUID();

        List<MemberBalanceResponse> balances = List.of(
                new MemberBalanceResponse(alice, "Alice", "a@test.com", new BigDecimal("50.0000")),
                new MemberBalanceResponse(bob, "Bob", "b@test.com", new BigDecimal("-50.0000")));

        List<SimplifiedDebtResponse> debts = service.simplify(balances);

        assertEquals(1, debts.size());
        assertEquals(bob, debts.getFirst().fromUserId());
        assertEquals(alice, debts.getFirst().toUserId());
        assertEquals(new BigDecimal("50.0000"), debts.getFirst().amount());
    }
}
