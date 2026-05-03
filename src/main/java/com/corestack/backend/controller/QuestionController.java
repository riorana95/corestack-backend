package com.corestack.backend.controller;

// DTO used to receive request data from the client for question create APIs.

import com.corestack.backend.dto.PageResponseDTO;
import com.corestack.backend.dto.QuestionRequestDTO;
import com.corestack.backend.dto.QuestionResponseDTO;
import com.corestack.backend.entity.CompanyEntity;
import com.corestack.backend.entity.QuestionEntity;
import com.corestack.backend.repository.CompanyRepository;
import com.corestack.backend.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// Entity representing the company table.
// Entity representing the questions table.
// Repository used here to validate and load an existing company by id.
// Service layer that handles question-related database operations.
// Used to return a 404 HTTP status if the company id does not exist.
// Used with HttpStatus to throw proper REST errors.
// Spring annotations used to map HTTP requests to methods.
// List is used for returning multiple questions.
// Collectors is used to convert a stream into a List for batch insert.

// Marks this class as a REST controller so methods return JSON responses directly.
@RestController
public class QuestionController {

    // Service dependency used for saving and fetching questions.
    private final QuestionService questionService;
    // Repository dependency used to find the company for a given companyId.
    private final CompanyRepository companyRepository;

    // Constructor injection lets Spring provide these dependencies automatically.
    public QuestionController(QuestionService questionService, CompanyRepository companyRepository) {
        this.questionService = questionService;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/questions")
    public PageResponseDTO<QuestionResponseDTO> getQuestion(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return questionService.getFilteredQuestions(companyName, tag, page, size);
    }

    // Maps HTTP GET /question?companyId=1 to this method.
    @GetMapping("/questionBy")
    // Reads companyId from the query string and returns all questions for that company.
    public List<QuestionEntity> getQuestionsByCompany(@RequestParam Long companyId){
        return questionService.getQuestionsByCompanyId(companyId);
    }

    // Maps HTTP POST /question to create one question from JSON request body.
    @PostMapping("/question")
    // @RequestBody tells Spring to convert incoming JSON into QuestionRequest
    // object.
    public QuestionEntity createQuestion(@RequestBody QuestionRequestDTO request) {
        return questionService.createQuestion(toQuestion(request));
    }

    @PutMapping("/question/{id}")
    public QuestionEntity updateQuestion(@PathVariable Long id, @RequestBody QuestionRequestDTO request) {
        validateMatchingIds(id, request);
        return questionService.updateQuestion(id, toQuestion(request));
    }

    // Maps HTTP POST /question/batch to create multiple questions at once.
    @PostMapping("/question/batch")
    // Accepts a JSON array, converts each request into Question entity, then saves
    // all.
    public List<QuestionEntity> createQuestions(@RequestBody List<QuestionRequestDTO> requests) {
        return questionService.createQuestions(
                requests.stream().map(this::toQuestion).collect(Collectors.toList()));
    }

    // Helper method used internally to convert request DTO into Question entity.
    private QuestionEntity toQuestion(QuestionRequestDTO request) {
        // Create an empty Question object that we will fill from request data.
        QuestionEntity questionEntity = new QuestionEntity();
        // Copy question text from request JSON to entity field.
        questionEntity.setQuestion(request.getQuestion());
        // Copy long description from request JSON.
        questionEntity.setDescription(request.getDescription());
        // Copy difficulty such as beginner/intermediate/advanced.
        questionEntity.setDifficulty(request.getDifficulty());
        // Copy content type such as mixed/text/code.
        questionEntity.setContentType(request.getContentType());
        // Copy extra content if present.
        questionEntity.setContent(request.getContent());
        // Copy tags list like ["project", "angular"].
        questionEntity.setTags(request.getTags());

        // Find the company row from database using companyId from the request.
        CompanyEntity companyEntity = companyRepository.findById(request.getCompanyId())
                // If no matching company exists, return HTTP 404 instead of saving invalid
                // data.
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company not found with id " + request.getCompanyId()));
        // Link this question to the found company.
        questionEntity.setCompanyEntity(companyEntity);

        // Return the fully prepared entity to the service layer for saving.
        return questionEntity;
    }

    private void validateMatchingIds(Long id, QuestionRequestDTO request) {
        if (request.getId() != null && !id.equals(request.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Path id and request id must match");
        }
    }
}
