# MediConnect AI — Healthcare Management & AI Assistant Platform

A bilingual (Arabic / English) clinic-management platform for private clinics
and medical centers in Jordan. It manages patients, doctors, appointments,
electronic medical records, prescriptions, notifications and analytics, and
includes a **guarded, informational AI symptom assistant** that helps patients
navigate care — without ever diagnosing or prescribing.

> **Portfolio / demonstration project.** It uses **only fictional data**. It is
> not a certified medical device and must not be used for real clinical
> decision-making without appropriate regulatory, security and clinical review.

---

## Table of contents

- [What is implemented](#what-is-implemented)
- [Architecture at a glance](#architecture-at-a-glance)
- [Technology stack](#technology-stack)
- [Quick start (Docker Compose)](#quick-start-docker-compose)
- [Local development](#local-development)
- [Demo credentials](#demo-credentials)
- [Testing](#testing)
- [Documentation](#documentation)
- [Safety, security & privacy](#safety-security--privacy)
- [Project status & roadmap](#project-status--roadmap)

---

## What is implemented

This repository is built as a **modular monolith** with clear module boundaries.
The current codebase implements and **tests** the architecturally load-bearing
and safety-critical parts end to end:

| Area | Status |
|------|--------|
| Modular-monolith scaffold, shared kernel (errors, tenancy, correlation IDs) | ✅ Implemented |
| RFC 7807 error handling + validation | ✅ Implemented |
| JWT auth, rotating refresh-token families, BCrypt(12), role authorization | ✅ Implemented |
| Multi-tenancy (clinic_id discriminator + server-side tenant guard) | ✅ Implemented + tested |
| AI symptom assistant safety core (consent, red-flag interrupt, prompt-injection & output filtering, provider abstraction + mock) | ✅ Implemented + tested |
| Appointment status state machine + double-booking protection (overlap check + DB unique index) + idempotency | ✅ Implemented + tested |
| Clinical-note signing, immutability & append-only amendments | ✅ Implemented + tested |
| Flyway schema + fictional seed data | ✅ Implemented |
| Frontend: i18n AR/EN, full RTL, accessible components, AI-safety UI | ✅ Implemented + tested |
| Docker/Compose, Nginx, GitHub Actions CI (build, test, e2e, dep-scan, CodeQL) | ✅ Implemented |
| Remaining CRUD breadth (all admin/reception/doctor screens, notifications, analytics, privacy workflows) | 🚧 Designed & documented; partially built |

See [`docs/ROADMAP.md`](docs/ROADMAP.md) for what is designed-but-not-yet-built
and the intended sequence. The design docs describe the **full** system; the
code prioritizes a vertical slice that is real, compiling and tested over broad
but shallow stubs.

## Architecture at a glance

```
frontend (React/TS/Vite/MUI, i18n + RTL)  ──HTTP──▶  backend (Spring Boot, Java 21)
                                                        │
              modular monolith: identity · clinic · patient · scheduling
              medicalrecord · prescription · notification · aiassistant
              analytics · audit · privacy · shared
                                                        │
                                   PostgreSQL (Flyway)      Redis
```

Full detail in [`ARCHITECTURE.md`](ARCHITECTURE.md).

## Technology stack

- **Backend:** Java 21, Spring Boot 3.3, Spring Web / Security / Data JPA, Bean
  Validation, PostgreSQL, Flyway, Redis, springdoc-openapi, JUnit 5, Mockito,
  Testcontainers, structured JSON logging.
- **Frontend:** React 18, TypeScript, Vite, React Router, TanStack Query, React
  Hook Form, Zod, Material UI, i18next, Recharts, Vitest, React Testing Library,
  Playwright.
- **Infra:** Docker, Docker Compose, Nginx, GitHub Actions.

## Quick start (Docker Compose)

Requires Docker with the Compose plugin.

```bash
# From the repository root
docker compose up --build
```

Then open:

- Frontend: <http://localhost:8081>
- Backend API + Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health: <http://localhost:8080/actuator/health>

The backend applies Flyway migrations (schema + fictional seed data) on start.

> Compose ships with **demo secrets** for convenience. For any non-local use,
> override `JWT_SECRET`, `DB_PASSWORD`, etc. via environment or a secret manager
> (see [`docs/DEPLOYMENT.md`](docs/DEPLOYMENT.md)).

## Local development

**Backend**
```bash
cd backend
mvn spring-boot:run     # needs a local PostgreSQL + Redis, or use compose for db/redis
mvn test                # unit + (Docker-gated) integration tests
```

**Frontend**
```bash
cd frontend
npm install
npm run dev             # http://localhost:5173, proxies /api to :8080
npm test                # Vitest unit + component tests
npm run e2e             # Playwright end-to-end (builds & previews first)
```

## Demo credentials

All fictional demo accounts share the password **`Demo1234!Pass`**.
(See [`docs/DEMO_CREDENTIALS.md`](docs/DEMO_CREDENTIALS.md).)

| Role | Email |
|------|-------|
| System admin | `sysadmin@demo.mediconnect.local` |
| Clinic admin | `admin@demo.mediconnect.local` |
| Doctor | `doctor@demo.mediconnect.local` |
| Receptionist | `reception@demo.mediconnect.local` |
| Patient | `patient@demo.mediconnect.local` |

## Testing

- **Backend:** `cd backend && mvn test` runs the pure unit tests (safety rules,
  clinical-note immutability, appointment state machine, booking concurrency,
  tenant guard). `mvn verify` additionally runs the Testcontainers integration
  tests (repository + tenant isolation), which require a Docker engine and are
  **skipped automatically when none is available**.
- **Frontend:** `npm test` (Vitest + RTL) and `npm run e2e` (Playwright).

Current status (this repo): **53 backend tests** and **8 frontend tests**
passing locally; integration/e2e suites run in CI.

## Documentation

| Document | Purpose |
|----------|---------|
| [`REQUIREMENTS.md`](REQUIREMENTS.md) | Requirements, assumptions, risks |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | Modules, tenancy, data model, diagrams |
| [`SECURITY.md`](SECURITY.md) | Threat model and controls |
| [`PRIVACY.md`](PRIVACY.md) | Consent, data-subject rights, retention |
| [`AI_SAFETY.md`](AI_SAFETY.md) | AI guardrails and safety design |
| [`docs/API.md`](docs/API.md) | API conventions and key endpoints |
| [`docs/FHIR_MAPPING.md`](docs/FHIR_MAPPING.md) | FHIR resource mappings |
| [`docs/DEPLOYMENT.md`](docs/DEPLOYMENT.md) | Deployment guide |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Future development |
| [`docs/diagrams/`](docs/diagrams) | PlantUML diagrams (component, ERD, sequences) |
| [`api/bruno/`](api/bruno) | Bruno API collection |

## Safety, security & privacy

The AI assistant is **informational and care-navigation only**. By design it:
never diagnoses, never prescribes, interrupts on emergency red flags **before**
any model call, requires explicit consent, minimizes/de-identifies data sent to
providers, filters diagnosis/prescription language out of model output, and
never writes to a medical record without doctor review. See
[`AI_SAFETY.md`](AI_SAFETY.md), [`SECURITY.md`](SECURITY.md) and
[`PRIVACY.md`](PRIVACY.md).

## Project status & roadmap

This is an actively-shaped portfolio build. The vertical slice is real and
tested; breadth is documented and in progress. See [`docs/ROADMAP.md`](docs/ROADMAP.md).

## License

For portfolio/demonstration use. No warranty. Not for clinical use.
