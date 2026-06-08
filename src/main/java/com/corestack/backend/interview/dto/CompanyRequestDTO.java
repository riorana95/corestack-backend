package com.corestack.backend.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyRequestDTO {
    private Long id;
    private String name;
    private String role;
    private String round;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
}
