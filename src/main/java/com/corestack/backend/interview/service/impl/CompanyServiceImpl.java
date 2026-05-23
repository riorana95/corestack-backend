package com.corestack.backend.interview.service.impl;

import com.corestack.backend.interview.entity.CompanyEntity;
import com.corestack.backend.interview.repository.CompanyRepository;
import com.corestack.backend.interview.service.CompanyService;
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
