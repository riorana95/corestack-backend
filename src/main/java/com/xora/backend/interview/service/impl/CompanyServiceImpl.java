package com.xora.backend.interview.service.impl;

import com.xora.backend.interview.dto.CompanyRequestDTO;
import com.xora.backend.interview.dto.CompanyResponseDTO;
import com.xora.backend.interview.entity.CompanyEntity;
import com.xora.backend.interview.mapper.CompanyMapper;
import com.xora.backend.interview.repository.CompanyRepository;
import com.xora.backend.interview.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link CompanyService} implementation. All writes are
 * {@code @Transactional} and return DTOs (never raw entities).
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {
        CompanyEntity entity = companyMapper.toEntity(request);
        return companyMapper.toResponse(companyRepository.save(entity));
    }

    @Override
    @Transactional
    public List<CompanyResponseDTO> createCompanies(List<CompanyRequestDTO> requests) {
        List<CompanyEntity> entities = requests.stream().map(companyMapper::toEntity).toList();
        return companyRepository.saveAll(entities).stream()
                .map(companyMapper::toResponse)
                .toList();
    }
}
