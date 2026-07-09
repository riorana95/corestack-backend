-- Interview Prep module schema (Phase 1).
-- Long IDs are kept for backward compat with existing data; Phase 2 will
-- migrate to UUIDs to match Split Vise.

CREATE TABLE IF NOT EXISTS questions (
    id              BIGSERIAL PRIMARY KEY,
    question        VARCHAR(1000) NOT NULL,
    description     TEXT,
    difficulty      VARCHAR(20)  NOT NULL,
    content_type    VARCHAR(20),
    content         TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_questions_difficulty
        CHECK (difficulty IN ('beginner', 'intermediate', 'advanced'))
);

CREATE TABLE IF NOT EXISTS companies (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    role            VARCHAR(200) NOT NULL,
    round           VARCHAR(100),
    date            DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS question_tags (
    question_id     BIGINT NOT NULL REFERENCES questions (id) ON DELETE CASCADE,
    tags            VARCHAR(100) NOT NULL,
    CONSTRAINT pk_question_tags PRIMARY KEY (question_id, tags)
);

CREATE TABLE IF NOT EXISTS question_companies (
    question_id     BIGINT NOT NULL REFERENCES questions (id) ON DELETE CASCADE,
    company_id      BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    CONSTRAINT pk_question_companies PRIMARY KEY (question_id, company_id)
);

CREATE INDEX IF NOT EXISTS idx_questions_difficulty ON questions (difficulty);
CREATE INDEX IF NOT EXISTS idx_companies_name_lower ON companies (LOWER(name));
CREATE INDEX IF NOT EXISTS idx_question_tags_tag ON question_tags (tags);
