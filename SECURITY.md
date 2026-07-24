# Security Design — MediConnect AI

This document describes the threat model and the security controls implemented
and designed for MediConnect AI. It is a portfolio design; a real deployment
would additionally require penetration testing, a formal risk assessment and
compliance review.

## 1. Assets & threat model (summary)

| Asset | Primary threats |
|-------|-----------------|
| Patient records (PHI) | Cross-tenant access, broken object-level authorization, exfiltration |
| Credentials / sessions | Credential stuffing, token theft/replay, weak hashing |
| Clinical notes & prescriptions | Tampering with signed records, repudiation |
| AI symptom data | Unintended provider exposure, prompt injection, over-collection |
| Audit trail | Tampering, gaps |

Trust boundaries: browser → API (untrusted input), API → database, API → AI
provider (data-minimized), API → notification providers.

## 2. Authentication & session security

- **Password hashing:** BCrypt with strength 12
  (`SecurityConfig.passwordEncoder`). Argon2id is a drop-in alternative
  (`Argon2PasswordEncoder`) documented for higher-assurance deployments.
- **Access tokens:** short-lived (15 min) stateless JWT (HS256) carrying only
  subject, tenant and roles (`JwtService`). No PHI in tokens.
- **Refresh tokens:** opaque, random, stored only as SHA-256 hashes, in a
  **rotating family** (`RefreshTokenService`). Each use rotates the token;
  presenting an already-rotated token (replay) **revokes the entire family** —
  defeating token theft. Logout revokes all of a user's tokens.
- **Login hardening:** identical error for unknown-user vs wrong-password (no
  account enumeration); failed-attempt counter on the account for lockout/rate
  policies; inactive/unverified accounts cannot authenticate.
- **MFA extension point:** the auth flow is structured so a second-factor step
  can be inserted after primary credential verification without reworking token
  issuance.

## 3. Authorization

- **Deny by default.** The security filter chain authenticates every request
  except an explicit public allowlist (`SecurityConfig`).
- **Role-based, server-side.** Roles come only from the signed token; the client
  cannot assert its own role. Method-level `@PreAuthorize` (enabled via
  `@EnableMethodSecurity`) adds per-endpoint checks.
- **Object-level / tenant ownership.** Every tenant-scoped operation verifies the
  object belongs to the caller's clinic via `TenantGuard` and clinic-scoped
  repository finders (e.g. `PatientRepository.findByIdAndClinicId`). Cross-tenant
  access is reported as **404**, never confirming existence across clinics.

## 4. Multi-tenancy isolation

- Shared database with a `clinic_id` discriminator on every tenant-scoped table.
- The tenant id is bound per-request from the JWT into `TenantContext` by
  `JwtAuthenticationFilter`, cleared after each request to prevent thread-pool
  leakage.
- Repositories never expose an unfiltered `findById` to service code for
  tenant-scoped entities; `TenantGuard.verifyOwnership` is the central check.
- Tested by `PatientTenantIsolationIT` (a record in clinic A is invisible to
  clinic B even with the correct id) and `TenantGuardTest`.

## 5. Input validation & injection defense

- All request DTOs use Bean Validation (`@Valid`), including a Jordanian `+962`
  phone pattern and a minimum password length.
- Persistence uses Spring Data JPA / parameterized queries — no string-built SQL.
- AI input is sanitized and wrapped against prompt injection; AI output is
  validated (see [`AI_SAFETY.md`](AI_SAFETY.md)).
- Validation failures return an RFC 7807 problem with a structured `errors`
  array (`GlobalExceptionHandler`).

## 6. Transport, headers & CORS

- Security headers set at the app tier (`SecurityConfig`) and for static assets
  in Nginx: `Content-Security-Policy`, `X-Content-Type-Options: nosniff`,
  `X-Frame-Options: DENY` / `frame-ancestors 'none'`, `Referrer-Policy:
  no-referrer`.
- **CORS is restricted** to a configured origin allowlist
  (`mediconnect.security.cors.allowed-origins`); credentials are only allowed
  for those origins. No wildcard with credentials.
- CSRF is disabled deliberately because the API is stateless and uses bearer
  tokens (no cookie-based auth); if cookie auth is introduced, CSRF protection
  must be re-enabled.

## 7. Concurrency & data integrity

- Optimistic locking (`@Version` on `BaseEntity`) guards concurrent edits;
  conflicts surface as RFC 7807 `409`.
- Double-booking is prevented by an application overlap check **and** a
  PostgreSQL partial unique index on `(doctor_id, start_time)` for active
  statuses — the authoritative guarantee under races (`BookingServiceTest`,
  `V1__baseline_schema.sql`).
- Idempotency keys make retried appointment creation and external notifications
  safe.

## 8. Signed-record integrity (non-repudiation)

Signed clinical notes are immutable; corrections are append-only amendments
carrying author, timestamp and reason (`ClinicalNote`, `ClinicalNoteAmendment`,
`ClinicalNoteTest`). This preserves the medico-legal record.

## 9. File uploads (design)

Document upload endpoints must: validate content type and size, store outside
the web root with generated names, prevent path traversal, and scan where
possible. (Upload storage is designed; see `Document` entity and roadmap.)

## 10. Secrets, logging & audit

- Secrets (`JWT_SECRET`, DB/Redis credentials, provider keys) come only from
  environment variables / secret managers — never committed. Compose defaults
  are clearly marked demo-only.
- **Structured JSON logging** with a per-request correlation id
  (`CorrelationIdFilter`, `logback-spring.xml`). Passwords, tokens, full
  clinical notes and unnecessary PHI are **never** logged.
- The audit module records login attempts, failed authorization, record access,
  amendments, prescriptions, exports, permission/consent changes, AI operations
  and administrative actions (see [`ARCHITECTURE.md`](ARCHITECTURE.md)).

## 11. Rate limiting

Auth and other sensitive endpoints are designed to be rate-limited (Redis-backed
token bucket keyed by IP + account). Redis is provisioned in the stack for this
and for idempotency/short-lived state.

## 12. Supply chain / CI security

GitHub Actions (`.github/workflows/ci.yml`) runs, on every push/PR: backend
build+test, frontend lint/test/build, Playwright e2e, **Trivy** filesystem
vulnerability scan + `npm audit`, and **CodeQL** for Java and JS/TS.

## 13. Responsible disclosure

For a real deployment, provide a `SECURITY.md` contact and coordinated
disclosure policy. This portfolio project has no production endpoint.
