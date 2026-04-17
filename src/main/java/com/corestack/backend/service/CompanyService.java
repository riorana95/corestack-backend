package com.corestack.backend.service;

import com.corestack.backend.entity.CompanyEntity;

import java.util.List;

public interface CompanyService {
    List<CompanyEntity> getAllCompanies();
    CompanyEntity createCompany(CompanyEntity companyEntity);
    List<CompanyEntity> createCompanies(List<CompanyEntity> companies);
}
