package com.xora.backend.interview.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xora.backend.common.entity.BaseAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Persistent company record (a company + role + round + date combo).
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "questions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CompanyEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String role;
    private String round;
    private LocalDate date;

    @ManyToMany(mappedBy = "companies")
    @JsonIgnore
    private Set<QuestionEntity> questions = new HashSet<>();
}
