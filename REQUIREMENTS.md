# Requirements, Assumptions & Risks — MediConnect AI

## 1. Objective

A bilingual (Arabic/English) clinic-management web application for private
clinics and medical centers in Jordan, covering patients, doctors, appointments,
electronic medical records, prescriptions, notifications, analytics, and a
guarded, informational AI symptom assistant.

## 2. User roles

`PATIENT`, `DOCTOR`, `NURSE`, `RECEPTIONIST`, `CLINIC_ADMIN`, `SYSTEM_ADMIN`,
`DATA_PROTECTION_OFFICER`.

## 3. Functional scope (by module)

Summarized; the authoritative list is the task brief. Highlights:

- **Auth:** registration, login/logout, email/phone verification, OTP password
  reset, refresh-token rotation, RBAC, MFA extension point, rate limiting,
  account activation/deactivation.
- **Clinic:** clinics, branches, departments, specialties, staff, doctor
  assignments, services, consultation duration, working hours, holidays.
- **Patient:** bilingual names, national id/passport, DOB, sex, contact,
  emergency contact, blood group, allergies, chronic conditions, insurance, MRN,
  preferred language, consent records, duplicate detection.
- **Scheduling:** availability, slots, booking, reschedule, cancel, waiting
  list, check-in, no-show, status history, double-booking protection, locking.
- **EMR:** encounter, complaint, history, vitals, exam, diagnosis, notes,
  treatment plan, follow-up, referrals, lab/imaging orders, documents; note
  draft/signed/amended with immutability + append-only amendments.
- **Prescription:** prescription + items (name, strength, form, dose, frequency,
  route, duration, instructions), previous prescriptions, discontinue, printable,
  demonstration-only medication warning UI.
- **Notification:** internal + email/SMS/WhatsApp abstractions, confirmations,
  reminders, cancellation/reschedule, follow-ups, delivery status, retry,
  idempotency, templates, opt-in.
- **AI assistant:** AR/EN chat, explicit consent, structured follow-ups,
  red-flag detection before external model, care-level recommendation, suggested
  specialty, structured summary, doctor-review status, deletion, prompt-injection
  defenses, I/O validation, no diagnosis, no prescribing, no auto-write to
  records, provider abstraction.
- **Analytics:** patient counts, new patients, status distribution, cancellation
  & no-show rates, doctor utilization, peak times, popular specialties, average
  wait, notification delivery stats, date/clinic/branch filters.
- **Privacy:** versioned notice, consent history/withdrawal, access/correction/
  deletion requests, communication prefs, retention status, request workflow,
  DPO dashboard, export, cross-border config.
- **Audit:** login attempts, failed authz, record access, creation, amendments,
  prescriptions, exports, permission/consent changes, AI ops, admin actions.

## 4. Non-functional requirements

- **Localization:** English + Arabic, complete RTL, Jordanian `+962` mobile
  validation, Amman timezone, JOD formatting, localized dates, accessible error
  messages, no dynamic sentence concatenation.
- **Accessibility:** WCAG 2.2 AA (keyboard, labels, focus, contrast, target
  size, screen-reader support, error summaries, no color-only meaning).
- **Security:** see [`SECURITY.md`](SECURITY.md).
- **Privacy:** see [`PRIVACY.md`](PRIVACY.md).
- **API:** `/api/v1`, OpenAPI, pagination/sorting/filtering/search, idempotency,
  versioning, correlation ids, consistent errors, correct status codes.
- **Observability:** structured JSON logs, health endpoints, correlation ids.

## 5. Assumptions

1. **Jurisdiction:** Jordan; Amman timezone; Arabic and English only for v1.
2. **Mobile numbers:** Jordanian mobiles are `+9627XXXXXXXX` (subscriber begins
   with 7). Landlines are out of scope for the mobile validator.
3. **AI provider:** defaults to a local **mock**; no external AI provider is
   contacted unless explicitly configured, consented and covered by a DPA.
4. **Emergency guidance:** 911 is the emergency number referenced for Jordan.
5. **Tenancy:** shared-DB `clinic_id` model for MVP; `SYSTEM_ADMIN` is the only
   cross-tenant role.
6. **Currency:** JOD, 3 decimal places.
7. **This is a portfolio build:** correctness of a tested vertical slice is
   prioritized over exhaustive CRUD breadth; the rest is designed and documented.

## 6. Risks & mitigations

| Risk | Mitigation |
|------|-----------|
| AI gives unsafe/diagnostic output | Pre-model red-flag interrupt, output filtering, consent gate, doctor review, mock default |
| Cross-tenant data leakage | Single tenant guard, clinic-scoped repos, 404 on cross-tenant, tenant-isolation tests |
| Double booking under load | Overlap check + DB partial unique index + optimistic lock + idempotency |
| Tampering with signed records | Immutability + append-only amendments |
| PHI in logs / to AI provider | Minimal logging policy; data-minimized AI requests |
| Secret leakage | Env/secret-manager only; demo secrets clearly marked |
| Localization defects (RTL, plurals) | Full RTL pipeline, i18next keys (no sentence concatenation), tests |
| Dependency vulnerabilities | Trivy + npm audit + CodeQL in CI |

## 7. Out of scope (v1)

Microservices, full FHIR server, billing/insurance claims adjudication, native
mobile apps, real-time video consultation. See [`docs/ROADMAP.md`](docs/ROADMAP.md).

## 8. Traceability

Safety requirements → [`AI_SAFETY.md`](AI_SAFETY.md); security requirements →
[`SECURITY.md`](SECURITY.md); privacy → [`PRIVACY.md`](PRIVACY.md); implemented
vs designed → [`README.md`](README.md#what-is-implemented) and
[`docs/ROADMAP.md`](docs/ROADMAP.md).
