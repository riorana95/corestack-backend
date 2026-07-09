package com.xora.backend.interview.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xora.backend.common.entity.BaseAuditEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Persistent interview question record.
 *
 * <p>Uses {@code @Getter/@Setter} instead of {@code @Data} because Lombok's
 * generated {@code equals/hashCode/toString} trigger lazy loading on JPA
 * entities. Companies are excluded from {@code toString} to avoid recursion.
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "companies")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class QuestionEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String question;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String difficulty;

    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_tags", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tags")
    private List<String> tags;

    @ManyToMany
    @JoinTable(
            name = "question_companies",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id"))
    @JsonIgnore
    private Set<CompanyEntity> companies = new HashSet<>();

    public Set<CompanyEntity> getCompanies() {
        if (companies == null) {
            companies = new HashSet<>();
        }
        return companies;
    }
}
