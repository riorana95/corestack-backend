package com.corestack.backend.bootstrap;

import com.corestack.backend.entity.SectionQuestionEntity;
import com.corestack.backend.enums.SectionType;
import com.corestack.backend.repository.SectionQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.seed.section-questions", havingValue = "true", matchIfMissing = true)
public class SectionQuestionDataInitializer implements CommandLineRunner {

    private final SectionQuestionRepository sectionQuestionRepository;

    public SectionQuestionDataInitializer(SectionQuestionRepository sectionQuestionRepository) {
        this.sectionQuestionRepository = sectionQuestionRepository;
    }

    @Override
    public void run(String... args) {
        if (sectionQuestionRepository.count() > 0) {
            return;
        }

        sectionQuestionRepository.saveAll(List.of(
                buildFrontendQuestion(
                        "What is the virtual DOM in React?",
                        "The virtual DOM is a lightweight in-memory representation of the real DOM. React compares updates against it first and then applies only the required changes to the browser DOM.",
                        "beginner",
                        buildContent(
                                "React uses a reconciliation step to detect the smallest DOM update set.",
                                List.of(
                                        "It reduces direct DOM mutations.",
                                        "It helps React batch updates efficiently.",
                                        "It improves rendering performance for frequent UI changes."
                                ),
                                null,
                                "const element = <h1>Hello</h1>;"
                        ),
                        List.of("react", "performance")
                ),
                buildFrontendQuestion(
                        "When should you clean up an effect in React?",
                        "You should clean up an effect when it creates subscriptions, timers, listeners, or pending async work that should be cancelled before the component unmounts or before the effect runs again.",
                        "intermediate",
                        buildContent(
                                "Cleanup prevents memory leaks and stale updates.",
                                List.of(
                                        "Remove event listeners.",
                                        "Clear intervals or timeouts.",
                                        "Abort in-flight requests when needed."
                                ),
                                null,
                                "useEffect(() => {\n  const timer = setInterval(fetchData, 5000);\n  return () => clearInterval(timer);\n}, []);"
                        ),
                        List.of("react", "hooks")
                ),
                buildFrontendQuestion(
                        "Flexbox vs Grid: when do you use each?",
                        "Flexbox is best for one-dimensional layouts such as rows or columns. Grid is better for two-dimensional layouts where you control both rows and columns together.",
                        "beginner",
                        buildContent(
                                "Choose the layout model based on whether the problem is one-dimensional or two-dimensional.",
                                List.of(
                                        "Flexbox aligns content in a single axis.",
                                        "Grid defines layout areas across rows and columns.",
                                        "Both can be combined in the same screen."
                                ),
                                buildTable(
                                        List.of("Feature", "Flexbox", "Grid"),
                                        List.of(
                                                List.of("Layout axis", "Single axis", "Two axes"),
                                                List.of("Best for", "Components", "Page sections")
                                        )
                                ),
                                null
                        ),
                        List.of("css", "layout")
                ),
                buildFrontendQuestion(
                        "Explain event bubbling and event capturing in JavaScript.",
                        "Capturing runs from the root down to the target element, while bubbling runs from the target back up through its ancestors. Most event handlers use bubbling by default.",
                        "beginner",
                        buildContent(
                                "Understanding propagation helps when building nested interactive components.",
                                List.of(
                                        "Use stopPropagation() only when required.",
                                        "Delegate events higher in the tree when helpful.",
                                        "Be careful with nested buttons or clickable containers."
                                ),
                                null,
                                "parent.addEventListener('click', handleParent);\nchild.addEventListener('click', handleChild);"
                        ),
                        List.of("javascript", "dom")
                ),
                buildFrontendQuestion(
                        "How does Angular change detection work?",
                        "Angular checks component bindings to detect state changes and update the view. It can run after async events like clicks, HTTP responses, or timers, and `OnPush` reduces unnecessary checks.",
                        "intermediate",
                        buildContent(
                                "Change detection keeps templates in sync with component state.",
                                List.of(
                                        "Default strategy checks more aggressively.",
                                        "OnPush relies on input reference changes and explicit triggers.",
                                        "Immutable updates work well with OnPush."
                                ),
                                null,
                                "@Component({\n  changeDetection: ChangeDetectionStrategy.OnPush\n})"
                        ),
                        List.of("angular", "performance")
                ),
                buildFrontendQuestion(
                        "What is debouncing, and where is it useful in the frontend?",
                        "Debouncing delays execution until a burst of events stops. It is useful for search inputs, resize listeners, and other high-frequency events where you do not want to process every trigger.",
                        "beginner",
                        buildContent(
                                "Debouncing improves UX and reduces unnecessary work.",
                                List.of(
                                        "Common with search suggestions.",
                                        "Helps avoid excessive API calls.",
                                        "Often paired with RxJS or utility helpers."
                                ),
                                null,
                                "searchInput.pipe(debounceTime(300), distinctUntilChanged())"
                        ),
                        List.of("javascript", "rxjs", "performance")
                ),
                buildBackendQuestion(
                        "What makes a REST API stateless?",
                        "A REST API is stateless when each request contains all the information needed to process it, and the server does not rely on session state stored between requests.",
                        "beginner",
                        buildContent(
                                "Stateless APIs are easier to scale horizontally.",
                                List.of(
                                        "Authentication is usually carried with each request.",
                                        "Servers can handle requests independently.",
                                        "Load balancing becomes simpler."
                                ),
                                null,
                                "GET /api/users/42"
                        ),
                        List.of("rest", "api")
                ),
                buildBackendQuestion(
                        "Authentication vs authorization in backend systems.",
                        "Authentication verifies who the user is. Authorization decides what that authenticated user is allowed to access or do.",
                        "beginner",
                        buildContent(
                                "Most secured systems perform authentication before authorization.",
                                List.of(
                                        "Login or token verification handles authentication.",
                                        "Roles and permissions handle authorization.",
                                        "Both are required for secure APIs."
                                ),
                                null,
                                null
                        ),
                        List.of("security", "spring-boot")
                ),
                buildBackendQuestion(
                        "Why are database indexes important?",
                        "Indexes improve read performance by helping the database locate rows faster, but they also add storage cost and can slow down writes because the index must be maintained.",
                        "intermediate",
                        buildContent(
                                "Good indexing balances query speed against insert and update overhead.",
                                List.of(
                                        "Add indexes for frequently filtered columns.",
                                        "Avoid indexing every column blindly.",
                                        "Review execution plans for expensive queries."
                                ),
                                buildTable(
                                        List.of("Operation", "Without index", "With index"),
                                        List.of(
                                                List.of("Read", "Slower scan", "Faster lookup"),
                                                List.of("Write", "Lower overhead", "Higher maintenance")
                                        )
                                ),
                                null
                        ),
                        List.of("database", "sql")
                ),
                buildBackendQuestion(
                        "What is transaction management?",
                        "Transaction management ensures a group of database operations either all succeed together or all roll back together when an error occurs.",
                        "intermediate",
                        buildContent(
                                "Transactions protect data consistency across multiple write operations.",
                                List.of(
                                        "Useful when several tables must be updated together.",
                                        "Rollback happens when an error interrupts the workflow.",
                                        "Spring commonly uses `@Transactional` for this."
                                ),
                                null,
                                "@Transactional\npublic void placeOrder(OrderRequest request) {\n  saveOrder(request);\n  reserveInventory(request);\n}"
                        ),
                        List.of("spring-boot", "database")
                ),
                buildBackendQuestion(
                        "What is idempotency in an API?",
                        "An operation is idempotent when making the same request multiple times results in the same final state as making it once. This is important for retries and network failure handling.",
                        "intermediate",
                        buildContent(
                                "Idempotency is especially important for payment, order, and callback APIs.",
                                List.of(
                                        "GET is naturally idempotent.",
                                        "PUT is typically designed to be idempotent.",
                                        "POST may need idempotency keys for safe retries."
                                ),
                                null,
                                "Idempotency-Key: 5cb4e2d1-order-123"
                        ),
                        List.of("api", "system-design")
                ),
                buildBackendQuestion(
                        "How do validation and global exception handling work in Spring Boot?",
                        "Validation checks request data before business logic runs, and global exception handling centralizes error responses so the API returns consistent status codes and messages.",
                        "intermediate",
                        buildContent(
                                "This combination improves correctness and keeps controllers simpler.",
                                List.of(
                                        "Use bean validation annotations on request DTOs.",
                                        "Use `@Valid` in controller methods.",
                                        "Use `@RestControllerAdvice` for consistent error responses."
                                ),
                                null,
                                "@PostMapping\npublic ResponseEntity<Void> create(@Valid @RequestBody UserRequest request) {\n  return ResponseEntity.ok().build();\n}"
                        ),
                        List.of("spring-boot", "validation")
                )
        ));
    }

    private SectionQuestionEntity buildFrontendQuestion(
            String question,
            String description,
            String difficulty,
            Map<String, Object> content,
            List<String> tags
    ) {
        return buildQuestion(SectionType.FRONTEND, question, description, difficulty, content, tags);
    }

    private SectionQuestionEntity buildBackendQuestion(
            String question,
            String description,
            String difficulty,
            Map<String, Object> content,
            List<String> tags
    ) {
        return buildQuestion(SectionType.BACKEND, question, description, difficulty, content, tags);
    }

    private SectionQuestionEntity buildQuestion(
            SectionType section,
            String question,
            String description,
            String difficulty,
            Map<String, Object> content,
            List<String> tags
    ) {
        SectionQuestionEntity sectionQuestionEntity = new SectionQuestionEntity();
        sectionQuestionEntity.setSection(section);
        sectionQuestionEntity.setQuestion(question);
        sectionQuestionEntity.setAnswer(description);
        sectionQuestionEntity.setDescription(description);
        sectionQuestionEntity.setDifficulty(difficulty);
        sectionQuestionEntity.setContentType("mixed");
        sectionQuestionEntity.setContent(content);
        sectionQuestionEntity.setTags(tags);
        return sectionQuestionEntity;
    }

    private Map<String, Object> buildContent(
            String text,
            List<String> list,
            Map<String, Object> table,
            String code
    ) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("text", text);
        content.put("list", list);
        content.put("table", table);
        content.put("code", code);
        return content;
    }

    private Map<String, Object> buildTable(List<String> headers, List<List<String>> rows) {
        Map<String, Object> table = new LinkedHashMap<>();
        table.put("headers", headers);
        table.put("rows", rows);
        return table;
    }
}
