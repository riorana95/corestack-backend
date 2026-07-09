package com.xora.backend.settlement.controller;

import com.xora.backend.settlement.dto.CreateSettlementRequest;
import com.xora.backend.settlement.dto.SettlementResponse;
import com.xora.backend.settlement.dto.SimplifiedDebtResponse;
import com.xora.backend.settlement.service.DebtSimplificationService;
import com.xora.backend.settlement.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}")
public class SettlementController {

    private final SettlementService settlementService;
    private final DebtSimplificationService debtSimplificationService;

    public SettlementController(SettlementService settlementService,
                                DebtSimplificationService debtSimplificationService) {
        this.settlementService = settlementService;
        this.debtSimplificationService = debtSimplificationService;
    }

    @GetMapping("/debts")
    public List<SimplifiedDebtResponse> getSimplifiedDebts(@PathVariable UUID groupId) {
        return debtSimplificationService.getSimplifiedDebts(groupId);
    }

    @GetMapping("/settlements")
    public List<SettlementResponse> listSettlements(@PathVariable UUID groupId) {
        return settlementService.listSettlements(groupId);
    }

    @PostMapping("/settlements")
    @ResponseStatus(HttpStatus.CREATED)
    public SettlementResponse createSettlement(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateSettlementRequest request) {
        return settlementService.createSettlement(groupId, request);
    }

    @PatchMapping("/settlements/{settlementId}/complete")
    public SettlementResponse completeSettlement(
            @PathVariable UUID groupId,
            @PathVariable UUID settlementId) {
        return settlementService.completeSettlement(groupId, settlementId);
    }

    @PatchMapping("/settlements/{settlementId}/cancel")
    public SettlementResponse cancelSettlement(
            @PathVariable UUID groupId,
            @PathVariable UUID settlementId) {
        return settlementService.cancelSettlement(groupId, settlementId);
    }
}
