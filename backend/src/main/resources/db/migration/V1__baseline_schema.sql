-- MediConnect AI — baseline schema (V1)
-- Shared-database multi-tenancy: tenant-scoped tables carry a clinic_id
-- discriminator that every server-side query filters on. All primary keys are
-- UUIDs; every table carries optimistic-lock version and audit timestamps.

-- ---------------------------------------------------------------------------
-- Tenancy & organisation
-- ---------------------------------------------------------------------------
CREATE TABLE clinic (
    id                 UUID PRIMARY KEY,
    version            BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    name_en            VARCHAR(200) NOT NULL,
    name_ar            VARCHAR(200),
    timezone           VARCHAR(64)  NOT NULL DEFAULT 'Asia/Amman',
    default_language   VARCHAR(8)   NOT NULL DEFAULT 'ar',
    active             BOOLEAN      NOT NULL DEFAULT true
);

CREATE TABLE branch (
    id                 UUID PRIMARY KEY,
    version            BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id          UUID        NOT NULL REFERENCES clinic(id),
    name_en            VARCHAR(200) NOT NULL,
    name_ar            VARCHAR(200),
    address_line       VARCHAR(400),
    phone_e164         VARCHAR(20)
);
CREATE INDEX ix_branch_clinic ON branch(clinic_id);

CREATE TABLE department (
    id                 UUID PRIMARY KEY,
    version            BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id          UUID        NOT NULL REFERENCES clinic(id),
    branch_id          UUID        REFERENCES branch(id),
    name_en            VARCHAR(200) NOT NULL,
    name_ar            VARCHAR(200),
    specialty          VARCHAR(120)
);
CREATE INDEX ix_department_clinic ON department(clinic_id);

-- ---------------------------------------------------------------------------
-- Identity
-- ---------------------------------------------------------------------------
CREATE TABLE user_account (
    id                    UUID PRIMARY KEY,
    version               BIGINT      NOT NULL DEFAULT 0,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id             UUID,
    email                 VARCHAR(320) NOT NULL,
    phone_e164            VARCHAR(20),
    password_hash         VARCHAR(200) NOT NULL,
    full_name_en          VARCHAR(200) NOT NULL,
    full_name_ar          VARCHAR(200),
    active                BOOLEAN      NOT NULL DEFAULT true,
    email_verified        BOOLEAN      NOT NULL DEFAULT false,
    phone_verified        BOOLEAN      NOT NULL DEFAULT false,
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    preferred_language    VARCHAR(8)   NOT NULL DEFAULT 'en',
    CONSTRAINT ux_user_email UNIQUE (email)
);
CREATE INDEX ix_user_clinic ON user_account(clinic_id);

CREATE TABLE user_role (
    user_id  UUID NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    role     VARCHAR(40) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE refresh_token (
    id          UUID PRIMARY KEY,
    version     BIGINT      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    user_id     UUID        NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    token_hash  VARCHAR(120) NOT NULL,
    family_id   UUID        NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN     NOT NULL DEFAULT false,
    rotated_to  UUID,
    CONSTRAINT ux_refresh_token_hash UNIQUE (token_hash)
);
CREATE INDEX ix_refresh_token_user ON refresh_token(user_id);
CREATE INDEX ix_refresh_token_family ON refresh_token(family_id);

-- ---------------------------------------------------------------------------
-- Patient
-- ---------------------------------------------------------------------------
CREATE TABLE patient (
    id                     UUID PRIMARY KEY,
    version                BIGINT      NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id              UUID        NOT NULL REFERENCES clinic(id),
    medical_record_number  VARCHAR(40) NOT NULL,
    full_name_en           VARCHAR(200) NOT NULL,
    full_name_ar           VARCHAR(200),
    national_id            VARCHAR(40),
    passport_number        VARCHAR(40),
    date_of_birth          DATE        NOT NULL,
    sex                    VARCHAR(16) NOT NULL,
    phone_e164             VARCHAR(20),
    email                  VARCHAR(320),
    blood_group            VARCHAR(8),
    preferred_language     VARCHAR(8)  NOT NULL DEFAULT 'ar',
    linked_user_id         UUID,
    CONSTRAINT ux_patient_mrn UNIQUE (clinic_id, medical_record_number)
);
CREATE INDEX ix_patient_clinic ON patient(clinic_id);
CREATE INDEX ix_patient_national_id ON patient(clinic_id, national_id);

-- ---------------------------------------------------------------------------
-- Scheduling
-- ---------------------------------------------------------------------------
CREATE TABLE appointment (
    id               UUID PRIMARY KEY,
    version          BIGINT      NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id        UUID        NOT NULL REFERENCES clinic(id),
    branch_id        UUID        NOT NULL,
    doctor_id        UUID        NOT NULL,
    patient_id       UUID        NOT NULL REFERENCES patient(id),
    start_time       TIMESTAMPTZ NOT NULL,
    end_time         TIMESTAMPTZ NOT NULL,
    status           VARCHAR(20) NOT NULL,
    idempotency_key  VARCHAR(120),
    reason           VARCHAR(500),
    CONSTRAINT ux_appointment_idempotency UNIQUE (idempotency_key),
    CONSTRAINT ck_appointment_time CHECK (end_time > start_time)
);
CREATE INDEX ix_appointment_clinic ON appointment(clinic_id);
CREATE INDEX ix_appointment_doctor_time ON appointment(doctor_id, start_time);
CREATE INDEX ix_appointment_patient ON appointment(patient_id);

-- Hard double-booking guard: no two slot-occupying appointments may share a
-- doctor and start time. This is the authoritative guarantee under concurrency.
CREATE UNIQUE INDEX ux_appointment_doctor_slot
    ON appointment (doctor_id, start_time)
    WHERE status IN ('REQUESTED', 'CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS');

CREATE TABLE appointment_status_history (
    id              UUID PRIMARY KEY,
    version         BIGINT      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    appointment_id  UUID        NOT NULL REFERENCES appointment(id) ON DELETE CASCADE,
    from_status     VARCHAR(20),
    to_status       VARCHAR(20) NOT NULL,
    changed_by      UUID
);
CREATE INDEX ix_appt_history_appt ON appointment_status_history(appointment_id);

-- ---------------------------------------------------------------------------
-- Medical records
-- ---------------------------------------------------------------------------
CREATE TABLE clinical_note (
    id            UUID PRIMARY KEY,
    version       BIGINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id     UUID        NOT NULL REFERENCES clinic(id),
    encounter_id  UUID        NOT NULL,
    author_id     UUID        NOT NULL,
    body          VARCHAR(20000) NOT NULL,
    status        VARCHAR(16) NOT NULL,
    signed_by     UUID,
    signed_at     TIMESTAMPTZ
);
CREATE INDEX ix_clinical_note_clinic ON clinical_note(clinic_id);
CREATE INDEX ix_clinical_note_encounter ON clinical_note(encounter_id);

CREATE TABLE clinical_note_amendment (
    id          UUID PRIMARY KEY,
    version     BIGINT      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    note_id     UUID        NOT NULL REFERENCES clinical_note(id) ON DELETE CASCADE,
    author_id   UUID        NOT NULL,
    text        VARCHAR(20000) NOT NULL,
    reason      VARCHAR(500) NOT NULL
);
CREATE INDEX ix_note_amendment_note ON clinical_note_amendment(note_id);

-- ---------------------------------------------------------------------------
-- AI assistant
-- ---------------------------------------------------------------------------
CREATE TABLE ai_conversation (
    id                 UUID PRIMARY KEY,
    version            BIGINT      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    clinic_id          UUID        NOT NULL REFERENCES clinic(id),
    patient_id         UUID        NOT NULL REFERENCES patient(id),
    language           VARCHAR(8)  NOT NULL DEFAULT 'en',
    consent_granted    BOOLEAN     NOT NULL DEFAULT false,
    consent_granted_at TIMESTAMPTZ,
    deleted            BOOLEAN     NOT NULL DEFAULT false
);
CREATE INDEX ix_ai_conversation_patient ON ai_conversation(patient_id);
