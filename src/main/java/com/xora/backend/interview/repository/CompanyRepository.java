package com.xora.backend.interview.repository;

import com.xora.backend.interview.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

}
