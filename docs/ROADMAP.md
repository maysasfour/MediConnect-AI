# Roadmap — MediConnect AI

This project ships a **real, tested vertical slice** plus a **full design**. This
roadmap states honestly what is built vs. designed, and the intended sequence.

## Status legend
✅ implemented & tested · 🟡 partially implemented · 📐 designed & documented

## Built now (✅)

- Modular-monolith scaffold + shared kernel (RFC 7807 errors, tenancy,
  correlation ids, UUID/optimistic-lock base entities, JSON logging).
- Identity/auth: register, login, refresh-token rotation with family revocation,
  logout, BCrypt(12), stateless JWT, server-side RBAC, restricted CORS + headers.
- Multi-tenancy: `clinic_id` discriminator, `TenantContext`/`TenantGuard`,
  clinic-scoped repositories; tenant-isolation integration test.
- AI symptom assistant safety core: consent gate, bilingual red-flag interrupt,
  prompt-injection + output filtering, provider abstraction + mock; unit-tested.
- Scheduling: appointment status state machine, double-booking protection
  (overlap check + DB partial unique index), idempotency; unit-tested.
- Medical records: clinical-note signing/immutability + append-only amendments;
  unit-tested.
- Flyway schema for all core tables + fictional seed; frontend (i18n/RTL/a11y,
  AI-safety UI) with Vitest + Playwright; Docker/Compose/Nginx; CI with
  dependency scanning + CodeQL.

## Near term (🟡 → ✅)

1. **Patient module breadth:** full CRUD DTOs/controllers, emergency contact,
   insurance, allergies/conditions, duplicate-detection endpoint + UI.
2. **Scheduling surface:** availability computation from `DoctorSchedule` +
   `ScheduleException`, slot API, waiting list, reschedule/cancel/check-in/no-show
   endpoints and reception/doctor calendars.
3. **EMR surface:** encounter workspace, vitals, diagnosis, orders, documents
   (with upload validation + path-traversal protection), timeline UI.
4. **Prescription:** builder UI, previous prescriptions, discontinue, printable
   output, demonstration-only medication warning UI.
5. **AI persistence & endpoints:** conversation/message/summary persistence,
   doctor-review workflow to promote a summary into the record.

## Mid term (📐 → 🟡)

6. **Notifications:** email/SMS/WhatsApp provider adapters, templates, delivery
   status, retry with idempotency, opt-in enforcement, reminder scheduler.
7. **Analytics:** KPI queries + Recharts dashboards with date/clinic/branch
   filters; average wait, utilization, peak times, delivery stats.
8. **Privacy workflows:** privacy-request lifecycle, DPO dashboard, data export,
   consent history/withdrawal UI, retention jobs, cross-border config.
9. **Audit:** complete event coverage + admin audit-log viewer with filters.
10. **Auth hardening:** email/phone verification, OTP password reset, Redis rate
    limiting, MFA (TOTP) via the extension point, account (de)activation UI.

## Longer term (📐)

11. **FHIR:** expose read APIs mapping to Patient/Practitioner/Appointment/
    Encounter/Observation/Condition/MedicationRequest/DocumentReference (see
    [`FHIR_MAPPING.md`](FHIR_MAPPING.md)).
12. **Schema-per-tenant** option for large clinics (see
    [`ARCHITECTURE.md`](../ARCHITECTURE.md#3-multi-tenancy)).
13. **Real AI provider** integration under a DPA with consent + evaluation
    (safety, bias, AR/EN quality).
14. **Insurance/billing**, lab/imaging integrations, patient mobile app,
    tele-consultation.

## Quality/ops backlog

- Contract tests for OpenAPI; load tests for booking concurrency; accessibility
  audits (axe) in CI; SBOM generation; blue/green or canary deploys; per-tenant
  data-retention automation.
