package com.corestack.backend.controller;

import com.corestack.backend.dto.CompanyRequestDTO;
import com.corestack.backend.entity.CompanyEntity;
import com.corestack.backend.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController

public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService){
        this.companyService = companyService;
    }

    @GetMapping("/company")
    public List<CompanyEntity> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @PostMapping("/company")
    public CompanyEntity createCompany(@RequestBody CompanyRequestDTO request) {
        return companyService.createCompany(toCompany(request));
    }

    @PostMapping("/company/batch")
    public List<CompanyEntity> createCompanies(@RequestBody List<CompanyRequestDTO> requests) {
        return companyService.createCompanies(
                requests.stream().map(this::toCompany).collect(Collectors.toList())
        );
    }

    private CompanyEntity toCompany(CompanyRequestDTO request) {
        CompanyEntity companyEntity = new CompanyEntity();
        companyEntity.setName(request.getName());
        companyEntity.setRole(request.getRole());
        return companyEntity;
    }
}
