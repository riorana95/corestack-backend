package com.corestack.backend.interview.repository;

import com.corestack.backend.interview.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

}
