package com.xora.backend.interview.service;

import com.xora.backend.interview.dto.CompanyRequestDTO;
import com.xora.backend.interview.dto.CompanyResponseDTO;

import java.util.List;

/**
 * Interview-prep company service.
 */
public interface CompanyService {

    List<CompanyResponseDTO> getAllCompanies();

    CompanyResponseDTO createCompany(CompanyRequestDTO request);

    List<CompanyResponseDTO> createCompanies(List<CompanyRequestDTO> requests);
}
