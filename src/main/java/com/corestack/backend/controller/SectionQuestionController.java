package com.corestack.backend.controller;

import com.corestack.backend.dto.SectionQuestionRequestDTO;
import com.corestack.backend.entity.SectionQuestionEntity;
import com.corestack.backend.enums.SectionType;
import com.corestack.backend.service.SectionQuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SectionQuestionController {

    private final SectionQuestionService sectionQuestionService;

    public SectionQuestionController(SectionQuestionService sectionQuestionService) {
        this.sectionQuestionService = sectionQuestionService;
    }

    @GetMapping("/questions/section/{sectionType}")
    public List<SectionQuestionRequestDTO> getQuestionsBySection(@PathVariable String sectionType) {
        return sectionQuestionService.getQuestionsBySection(SectionType.fromValue(sectionType));
    }

    @PostMapping("/questions/section")
    public List<SectionQuestionEntity> createSectionQuestion(
            @RequestBody List<SectionQuestionRequestDTO> request
    ){
        return sectionQuestionService.createSection(
                request
        );
    }


}
