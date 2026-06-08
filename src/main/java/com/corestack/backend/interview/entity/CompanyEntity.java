package com.corestack.backend.interview.entity;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@jakarta.persistence.Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "questions")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role;

    private String round;
    private LocalDate date;

    // Reverse side of many-to-many relationship: one company can have many
    // questions.
    @ManyToMany(mappedBy = "companies")
    @JsonIgnore
    // The questions that belong to this company.
    private Set<QuestionEntity> questions = new HashSet<>();
}
