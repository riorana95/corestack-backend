-- Splitwise module schema (sw_ prefix; does not touch interview tables)

CREATE TABLE sw_users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    password_hash   VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sw_users_email UNIQUE (email),
    CONSTRAINT chk_sw_users_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE TABLE sw_user_auth_providers (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID NOT NULL REFERENCES sw_users (id) ON DELETE CASCADE,
    provider          VARCHAR(20) NOT NULL,
    provider_user_id  VARCHAR(255) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sw_provider_identity UNIQUE (provider, provider_user_id),
    CONSTRAINT uk_sw_user_provider UNIQUE (user_id, provider),
    CONSTRAINT chk_sw_provider CHECK (provider IN ('LOCAL', 'GOOGLE'))
);

CREATE TABLE sw_groups (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(150) NOT NULL,
    description   VARCHAR(500),
    currency_code CHAR(3) NOT NULL DEFAULT 'USD',
    created_by    UUID NOT NULL REFERENCES sw_users (id),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE sw_group_members (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id   UUID NOT NULL REFERENCES sw_groups (id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES sw_users (id) ON DELETE CASCADE,
    role       VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    status     VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    joined_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sw_group_member UNIQUE (group_id, user_id),
    CONSTRAINT chk_sw_group_role CHECK (role IN ('ADMIN', 'MEMBER')),
    CONSTRAINT chk_sw_group_member_status CHECK (status IN ('ACTIVE', 'LEFT'))
);

CREATE INDEX idx_sw_group_members_group ON sw_group_members (group_id, status);
CREATE INDEX idx_sw_group_members_user ON sw_group_members (user_id, status);

CREATE TABLE sw_expenses (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id        UUID NOT NULL REFERENCES sw_groups (id) ON DELETE CASCADE,
    paid_by_user_id UUID NOT NULL REFERENCES sw_users (id),
    description     VARCHAR(500) NOT NULL,
    amount          NUMERIC(19, 4) NOT NULL,
    currency_code   CHAR(3) NOT NULL,
    expense_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    split_type      VARCHAR(20) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sw_expense_amount CHECK (amount > 0),
    CONSTRAINT chk_sw_split_type CHECK (split_type IN ('EQUAL', 'EXACT', 'PERCENTAGE'))
);

CREATE INDEX idx_sw_expenses_group_date ON sw_expenses (group_id, expense_date DESC);

CREATE TABLE sw_expense_splits (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id    UUID NOT NULL REFERENCES sw_expenses (id) ON DELETE CASCADE,
    user_id       UUID NOT NULL REFERENCES sw_users (id),
    share_amount  NUMERIC(19, 4) NOT NULL,
    share_percent NUMERIC(7, 4),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sw_expense_split_user UNIQUE (expense_id, user_id),
    CONSTRAINT chk_sw_share_amount CHECK (share_amount >= 0)
);

CREATE INDEX idx_sw_expense_splits_expense ON sw_expense_splits (expense_id);

CREATE TABLE sw_settlements (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id       UUID NOT NULL REFERENCES sw_groups (id) ON DELETE CASCADE,
    from_user_id   UUID NOT NULL REFERENCES sw_users (id),
    to_user_id     UUID NOT NULL REFERENCES sw_users (id),
    amount         NUMERIC(19, 4) NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note           VARCHAR(500),
    settled_at     TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sw_settlement_amount CHECK (amount > 0),
    CONSTRAINT chk_sw_settlement_users CHECK (from_user_id <> to_user_id),
    CONSTRAINT chk_sw_settlement_status CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX idx_sw_settlements_group ON sw_settlements (group_id, status);
