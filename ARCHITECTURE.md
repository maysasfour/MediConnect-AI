# Architecture — MediConnect AI

## 1. Style: modular monolith

MediConnect AI is a **modular monolith**: a single deployable Spring Boot
application partitioned into modules with explicit boundaries. This gives strong
consistency (single database, transactions, no distributed-systems tax) while
keeping module seams clean enough to extract services later if scale demands it.
Microservices are intentionally **out of scope** for v1.

### Modules

```
com.mediconnect
├── shared          cross-cutting kernel: errors (RFC 7807), tenancy, security,
│                   base entities (UUID + optimistic lock), correlation ids, web
├── identity        users, roles, JWT, refresh-token rotation, auth
├── clinic          clinics, branches, departments, services, schedules
├── patient         patient records, emergency contacts, insurance, consent links
├── scheduling      appointments, status history, availability, double-booking
├── medicalrecord   encounters, vitals, clinical notes (sign/amend), diagnoses,
│                   allergies, conditions, orders, documents
├── prescription    prescriptions & items, discontinue, printable output
├── notification    internal + email/SMS/WhatsApp abstractions, templates, retry
├── aiassistant     guarded symptom triage, provider abstraction, mock provider
├── analytics       operational KPIs and dashboards
├── audit           append-only audit trail
└── privacy         consent, privacy requests, DPO workflow, export
```

**Dependency rule:** feature modules depend on `shared`, never on each other's
internals. Cross-module interaction uses published DTOs/application services.
This keeps the dependency graph acyclic.

## 2. Cross-cutting concerns (the `shared` kernel)

- **Identifiers:** application-assigned **UUID** primary keys (`BaseEntity`),
  safe to expose in URLs.
- **Concurrency:** `@Version` optimistic locking on every entity.
- **Tenancy:** `TenantAwareEntity` adds `clinic_id`; `TenantContext` holds the
  per-request tenant; `TenantGuard` verifies object ownership.
- **Errors:** `GlobalExceptionHandler` emits RFC 7807 `application/problem+json`
  with a stable `type`, machine-readable `code`, correlation id and timestamp.
- **Observability:** `CorrelationIdFilter` + structured JSON logging.
- **Security:** stateless JWT filter binds both the security context and the
  tenant.

## 3. Multi-tenancy

**Strategy (v1):** shared schema, `clinic_id` discriminator, **server-side**
filtering on every tenant-scoped query. The tenant is derived only from the
signed JWT, never from client input.

**Why:** simplest operationally, strong isolation when discipline is enforced by
a single guard (`TenantGuard`) and clinic-scoped repositories, and cheap to run
for many small clinics.

**Path to schema-per-tenant (documented, not built):** introduce a
`TenantConnectionProvider` / `MultiTenantConnectionProvider` with Hibernate's
`SCHEMA` multi-tenancy, resolve the schema from `TenantContext`, and run Flyway
per schema. Because all access already flows through `TenantContext` and scoped
repositories, switching the physical isolation model does not touch business
logic. A hybrid (large clinics on dedicated schemas, small clinics shared) is
possible by resolving strategy per clinic.

## 4. Request lifecycle

```
Client ─▶ CorrelationIdFilter ─▶ JwtAuthenticationFilter ─▶ SecurityFilterChain
        ─▶ Controller (@Valid DTO) ─▶ Application service (tenant guard,
           business rules, transactions) ─▶ Repository (clinic-scoped) ─▶ DB
        ◀─ DTO response  /  RFC 7807 problem on error
```

## 5. Key domain rules encoded in code

- **Appointments:** explicit status state machine (`AppointmentStatus`) rejects
  illegal transitions; double booking blocked by overlap check + DB partial
  unique index; idempotency key for safe retries.
- **Clinical notes:** `DRAFT → SIGNED` (immutable) → `AMENDED` via append-only
  amendments (author, timestamp, reason). Signed content is never mutated.
- **AI assistant:** consent → red-flag interrupt (pre-model) → input sanitize →
  provider → output validation. See [`AI_SAFETY.md`](AI_SAFETY.md).

## 6. Data model

All entities extend `BaseEntity` (UUID id, version, created/updated) and, where
tenant-scoped, `TenantAwareEntity` (`clinic_id`). The full entity list and the
Flyway baseline are in `backend/src/main/resources/db/migration/V1__baseline_schema.sql`.
An ERD is in [`docs/diagrams/erd.puml`](docs/diagrams/erd.puml).

Core aggregates:

- **Organisation:** Clinic → Branch → Department; StaffProfile, DoctorProfile.
- **People:** UserAccount (+ roles), Patient (+ EmergencyContact, InsurancePolicy,
  Consent).
- **Scheduling:** DoctorSchedule, ScheduleException, Appointment,
  AppointmentStatusHistory.
- **Clinical:** Encounter, VitalSign, Allergy, MedicalCondition, ClinicalNote,
  ClinicalNoteAmendment, Diagnosis, Prescription, PrescriptionItem, Medication,
  LabOrder, ImagingOrder, Document.
- **AI / privacy / audit:** AIConversation, AIMessage, AISymptomSummary,
  Consent, PrivacyRequest, AuditLog, Notification, NotificationTemplate,
  RefreshToken.

## 7. API

RESTful under `/api/v1`, OpenAPI via springdoc (`/swagger-ui.html`). Conventions:
pagination/sorting/filtering, correlation ids, idempotency keys for
appointment/notification creation, and consistent RFC 7807 errors. See
[`docs/API.md`](docs/API.md).

## 8. Frontend architecture

React + TypeScript + Vite. TanStack Query for server state; React Hook Form + Zod
for forms; MUI for components; i18next for AR/EN with **full RTL** via an emotion
cache + `stylis-plugin-rtl`, switched by document direction. Accessibility
(WCAG 2.2 AA) is a first-class concern: semantic landmarks, error summaries,
visible focus, ≥44px targets, and no color-only meaning (e.g. the emergency
banner uses an assertive live region + icon + text).

## 9. Diagrams

PlantUML sources in [`docs/diagrams/`](docs/diagrams):
- `component.puml` — modules and dependencies
- `erd.puml` — entity relationships
- `sequence-ai-triage.puml` — AI safety pipeline
- `sequence-booking.puml` — double-booking-safe appointment creation

## 10. Non-goals for v1

Microservices, full FHIR server, real external AI provider by default, native
mobile apps, and billing/claims processing. Several are on the
[roadmap](docs/ROADMAP.md).
