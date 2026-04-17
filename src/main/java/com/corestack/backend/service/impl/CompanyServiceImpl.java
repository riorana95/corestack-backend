package com.corestack.backend.service.impl;

import com.corestack.backend.entity.CompanyEntity;
import com.corestack.backend.repository.CompanyRepository;
import com.corestack.backend.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<CompanyEntity> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public CompanyEntity createCompany(CompanyEntity companyEntity) {
        return companyRepository.save(companyEntity);
    }

    @Override
    public List<CompanyEntity> createCompanies(List<CompanyEntity> companies) {
        return companyRepository.saveAll(companies);
    }
}
