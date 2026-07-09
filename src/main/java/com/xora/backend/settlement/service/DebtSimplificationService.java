package com.xora.backend.settlement.service;

import com.xora.backend.common.util.MoneyUtil;
import com.xora.backend.expense.dto.GroupBalancesResponse;
import com.xora.backend.expense.dto.MemberBalanceResponse;
import com.xora.backend.expense.service.BalanceService;
import com.xora.backend.settlement.dto.SimplifiedDebtResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DebtSimplificationService {

    private final BalanceService balanceService;

    public DebtSimplificationService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public List<SimplifiedDebtResponse> getSimplifiedDebts(UUID groupId) {
        GroupBalancesResponse balances = balanceService.getGroupBalances(groupId);
        return simplify(balances.balances());
    }

    List<SimplifiedDebtResponse> simplify(List<MemberBalanceResponse> memberBalances) {
        List<BalanceNode> debtors = new ArrayList<>();
        List<BalanceNode> creditors = new ArrayList<>();

        for (MemberBalanceResponse balance : memberBalances) {
            BigDecimal net = balance.netBalance();
            if (net.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new BalanceNode(
                        balance.userId(),
                        balance.displayName(),
                        net.abs()));
            } else if (net.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new BalanceNode(
                        balance.userId(),
                        balance.displayName(),
                        net));
            }
        }

        debtors.sort(Comparator.comparing(BalanceNode::amount).reversed());
        creditors.sort(Comparator.comparing(BalanceNode::amount).reversed());

        List<SimplifiedDebtResponse> debts = new ArrayList<>();
        int debtorIndex = 0;
        int creditorIndex = 0;

        while (debtorIndex < debtors.size() && creditorIndex < creditors.size()) {
            BalanceNode debtor = debtors.get(debtorIndex);
            BalanceNode creditor = creditors.get(creditorIndex);
            BigDecimal amount = debtor.amount().min(creditor.amount());

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                debts.add(new SimplifiedDebtResponse(
                        debtor.userId(),
                        debtor.displayName(),
                        creditor.userId(),
                        creditor.displayName(),
                        MoneyUtil.normalize(amount)));
            }

            debtor = debtor.withReduced(amount);
            creditor = creditor.withReduced(amount);
            debtors.set(debtorIndex, debtor);
            creditors.set(creditorIndex, creditor);

            if (debtor.amount().compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }
            if (creditor.amount().compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }
        }

        return debts;
    }

    private record BalanceNode(UUID userId, String displayName, BigDecimal amount) {
        BalanceNode withReduced(BigDecimal paid) {
            return new BalanceNode(userId, displayName, amount.subtract(paid));
        }
    }
}
