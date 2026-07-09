package com.xora.backend.interview.controller;

import com.xora.backend.interview.dto.CompanyRequestDTO;
import com.xora.backend.interview.dto.CompanyResponseDTO;
import com.xora.backend.interview.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the Interview Prep company bank.
 * All routes are prefixed with {@code /api/v1/interview} and require authentication.
 */
@RestController
@RequestMapping("/api/v1/interview")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO request) {
        CompanyResponseDTO created = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/companies/batch")
    public List<CompanyResponseDTO> createCompanies(@Valid @RequestBody List<@Valid CompanyRequestDTO> requests) {
        return companyService.createCompanies(requests);
    }
}
