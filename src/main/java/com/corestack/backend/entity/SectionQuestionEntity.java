package com.corestack.backend.entity;

import com.corestack.backend.enums.SectionType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "section_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SectionType section;

    @Column(nullable = false)
    private String question;

    @Column(length = 4000)
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String difficulty;

    private String contentType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "section_question_tags",
            joinColumns = @JoinColumn(name = "section_question_id")
    )
    @Column(name = "tag")
    private List<String> tags;
}
