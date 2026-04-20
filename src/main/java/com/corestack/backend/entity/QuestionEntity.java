package com.corestack.backend.entity;

// JPA annotations used to map this Java class to database tables/columns.
import jakarta.persistence.*;
// Lombok generates a constructor with all fields.
import lombok.AllArgsConstructor;
// Lombok generates getters, setters, toString, equals and hashCode.
import lombok.Data;
// Lombok generates a no-argument constructor required by JPA.
import lombok.NoArgsConstructor;

// List is used because one question can have multiple tags.
import java.util.List;

// Marks this class as a JPA entity so Hibernate maps it to a database table.
@jakarta.persistence.Entity
// Tells Hibernate to use the table name "questions" in the database.
@Table(name="questions")
// Generates boilerplate methods like getters/setters for all fields.
@Data
// Generates an empty constructor.
@NoArgsConstructor
// Generates a constructor with all fields in order.
@AllArgsConstructor
public class QuestionEntity {

    // Marks this field as the primary key of the questions table.
    @Id
    // Tells the database to auto-generate the id value.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Unique id for each question row.
    // @Column(unique = true)//won't allow duplicate entry
    private Long id;

    // Stores the interview question text itself.
    private String question;

    // Maps description to a column with larger length because answers can be long.
    @Column(columnDefinition = "TEXT")
    // Detailed explanation or answer for the question.
    private String description;

    // Stores difficulty like beginner, intermediate or advanced.
    private String difficulty;

    // Stores what kind of content this question has, for example "mixed".
    private String contentType;

    // Stores extra content if needed; can be null.
    private String content;

    // Stores a list of simple String values in a separate table instead of a new entity.
    // EAGER is used so tags are loaded immediately with Question and JSON serialization does not fail.
    @ElementCollection(fetch = FetchType.EAGER)
    // List of tags like "project", "angular" or "backend".
    private List<String> tags;

    // Many questions can belong to one company, so this is a many-to-one relationship.
    @ManyToOne
    // Uses company_id as the foreign key column in the questions table.
    @JoinColumn(name = "company_id")
    // The company to which this question belongs.
    private CompanyEntity companyEntity;
}
