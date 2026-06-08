package com.corestack.backend.interview.controller;

import com.corestack.backend.interview.dto.CompanyRequestDTO;
import com.corestack.backend.interview.entity.CompanyEntity;
import com.corestack.backend.interview.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController

public class CompanyController {
    //
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/company")
    public List<CompanyEntity> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @PostMapping("/company/add")
    public CompanyEntity createCompany(@RequestBody CompanyRequestDTO request) {
        return companyService.createCompany(toCompany(request));
    }

    @PostMapping("/company/batch")
    public List<CompanyEntity> createCompanies(@RequestBody List<CompanyRequestDTO> requests) {
        return companyService.createCompanies(
                requests.stream().map(this::toCompany).collect(Collectors.toList()));
    }

    private CompanyEntity toCompany(CompanyRequestDTO request) {
        CompanyEntity companyEntity = new CompanyEntity();
        companyEntity.setName(request.getName());
        companyEntity.setRole(request.getRole());
        companyEntity.setRound(request.getRound());
        companyEntity.setDate(request.getDate());
        return companyEntity;
    }
}
