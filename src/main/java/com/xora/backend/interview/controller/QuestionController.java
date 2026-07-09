package com.xora.backend.interview.controller;

import com.xora.backend.interview.dto.PageResponseDTO;
import com.xora.backend.interview.dto.QuestionRequestDTO;
import com.xora.backend.interview.dto.QuestionResponseDTO;
import com.xora.backend.interview.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the Interview Prep question bank.
 *
 * <p>All routes are prefixed with {@code /api/v1/interview} and require
 * authentication (see {@link com.xora.backend.config.SecurityConfig}).
 */
@RestController
@RequestMapping("/api/v1/interview")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public PageResponseDTO<QuestionResponseDTO> getQuestions(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return questionService.getFilteredQuestions(companyName, tag, page, size);
    }

    @GetMapping("/questions/by-company")
    public List<QuestionResponseDTO> getQuestionsByCompany(@RequestParam Long companyId) {
        return questionService.getQuestionsByCompanyId(companyId);
    }

    @PostMapping("/questions")
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO request) {
        QuestionResponseDTO created = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/questions/{id}")
    public QuestionResponseDTO updateQuestion(@PathVariable Long id,
                                              @Valid @RequestBody QuestionRequestDTO request) {
        return questionService.updateQuestion(id, request);
    }

    @PostMapping("/questions/batch")
    public List<QuestionResponseDTO> createQuestions(@Valid @RequestBody List<@Valid QuestionRequestDTO> requests) {
        return questionService.createQuestions(requests);
    }
}
