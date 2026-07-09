package com.xora.backend.interview.service.impl;

import com.xora.backend.common.exception.BusinessException;
import com.xora.backend.common.exception.ErrorCode;
import com.xora.backend.common.exception.ResourceNotFoundException;
import com.xora.backend.interview.dto.PageResponseDTO;
import com.xora.backend.interview.dto.QuestionRequestDTO;
import com.xora.backend.interview.dto.QuestionResponseDTO;
import com.xora.backend.interview.entity.CompanyEntity;
import com.xora.backend.interview.entity.QuestionEntity;
import com.xora.backend.interview.mapper.QuestionMapper;
import com.xora.backend.interview.repository.CompanyRepository;
import com.xora.backend.interview.repository.QuestionRepository;
import com.xora.backend.interview.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default {@link QuestionService} implementation.
 *
 * <p>All write methods are {@code @Transactional}. Company references are
 * resolved inside the service so the controller stays thin.
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CompanyRepository companyRepository;
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               CompanyRepository companyRepository,
                               QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.companyRepository = companyRepository;
        this.questionMapper = questionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<QuestionResponseDTO> getFilteredQuestions(String companyName, String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionEntity> result = questionRepository.findFilteredQuestions(companyName, tag, pageable);
        List<QuestionResponseDTO> data = questionMapper.toResponseList(result.getContent());
        return new PageResponseDTO<>(
                data,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByCompanyId(Long companyId) {
        return questionMapper.toResponseList(questionRepository.findByCompanies_Id(companyId));
    }

    @Override
    @Transactional
    public QuestionResponseDTO createQuestion(QuestionRequestDTO request) {
        CompanyEntity company = requireCompany(request.companyId());
        QuestionEntity entity = questionMapper.toEntity(request, company);
        return questionMapper.toResponse(questionRepository.save(entity));
    }

    @Override
    @Transactional
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO request) {
        if (request.id() != null && !id.equals(request.id())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                    "Path id and request id must match", HttpStatus.BAD_REQUEST);
        }
        QuestionEntity existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + id));

        existing.setQuestion(request.question());
        existing.setDescription(request.description());
        existing.setDifficulty(request.difficulty());
        existing.setContentType(request.contentType());
        existing.setContent(request.content());
        existing.setTags(request.tags());

        // Replace companies (PUT semantics): remove all existing, add the one in the request.
        Set<CompanyEntity> newCompanies = new HashSet<>();
        if (request.companyId() != null) {
            newCompanies.add(requireCompany(request.companyId()));
        }
        existing.getCompanies().clear();
        existing.getCompanies().addAll(newCompanies);

        return questionMapper.toResponse(questionRepository.save(existing));
    }

    @Override
    @Transactional
    public List<QuestionResponseDTO> createQuestions(List<QuestionRequestDTO> requests) {
        List<QuestionEntity> entities = requests.stream()
                .map(req -> questionMapper.toEntity(req, requireCompany(req.companyId())))
                .toList();
        return questionMapper.toResponseList(questionRepository.saveAll(entities));
    }

    private CompanyEntity requireCompany(Long companyId) {
        if (companyId == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                    "companyId is required", HttpStatus.BAD_REQUEST);
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id " + companyId));
    }
}
