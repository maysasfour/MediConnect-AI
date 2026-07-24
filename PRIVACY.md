# Privacy Design — MediConnect AI

MediConnect AI handles health data, which is highly sensitive. This document
describes the privacy model: lawful, minimal processing; patient control; and
data-subject rights. It is written to be compatible with common data-protection
principles (purpose limitation, data minimization, storage limitation,
transparency, rights of access/rectification/erasure) and to map cleanly onto
Jordan's data-protection expectations for private healthcare providers.

> Portfolio note: **no real patient data** is used anywhere. All seed/test data
> is fictional.

## 1. Roles

- **Clinic (tenant)** — data controller for its patients' records.
- **MediConnect platform** — data processor providing the software.
- **Data Protection Officer (DPO)** — dedicated role
  (`DATA_PROTECTION_OFFICER`) with a review dashboard for privacy requests.

## 2. Data categories & minimization

| Category | Examples | Notes |
|----------|----------|-------|
| Identity | names (AR/EN), national id/passport, DOB, contact | Direct identifiers |
| Clinical | encounters, notes, diagnoses, prescriptions, vitals | Special-category data |
| AI symptom data | conversation turns, structured summaries | Consent-gated, minimized |
| Operational | appointments, notifications, audit | Needed for service |

**Data minimization for AI:** requests to an AI provider carry only sanitized
symptom text + language + prior turns — never names, national id, MRN, phone or
email (see [`AI_SAFETY.md`](AI_SAFETY.md)).

## 3. Consent

- **Versioned privacy notice.** Each consent references the notice version the
  patient agreed to, so consent history is auditable over time.
- **Granular communication opt-in** for email / SMS / WhatsApp reminders.
- **AI processing consent** is separate and explicit: no symptom text is
  processed or stored until the patient consents on the conversation. The UI
  presents the consent screen before the symptom input exists.
- **Withdrawal.** Consent can be withdrawn; withdrawal is recorded with a
  timestamp and stops the corresponding processing going forward.

## 4. Data-subject rights & privacy-request workflow

The `PrivacyRequest` entity + workflow supports:

- **Access** — export the patient's data (data-export function).
- **Rectification** — correction requests routed for review.
- **Erasure / anonymization** — delete or irreversibly anonymize, subject to
  legal retention limits for medical records.
- **Restriction / objection** — pause specific processing (e.g. communications).

Workflow states: `SUBMITTED → UNDER_REVIEW → (APPROVED|REJECTED) → COMPLETED`,
with DPO review. Every transition is audited. AI conversations are in scope for
access, correction and deletion; a patient can also delete a conversation
directly.

## 5. Retention

- Medical records follow clinical retention rules (configurable per clinic;
  typically multi-year) — erasure honours legally-required retention.
- Refresh tokens expire and are revoked; expired tokens are purged.
- Audit logs are retained per policy and protected from tampering.
- AI conversations: retained only while useful for care navigation and the
  patient's review, then deletable on request; summaries not accepted into the
  record do not become part of it.

## 6. Cross-border processing

A configuration flag governs whether AI or notification processing may occur
outside Jordan. Default posture is **in-region / no external AI provider** (mock
provider). Enabling an external provider requires: explicit configuration, an
approved data-processing agreement, patient consent, and continued data
minimization.

## 7. Transparency

- Public **privacy notice** and **AI disclaimer** screens (`/ai-disclaimer`).
- The assistant states, in the user's language, that it is informational only,
  does not diagnose or prescribe, and that summaries require doctor review.

## 8. Security of processing

Privacy depends on the controls in [`SECURITY.md`](SECURITY.md): tenant
isolation, object-level authorization, encryption in transit, minimal logging
(no PHI/secrets in logs), and audit trails.

## 9. DPO dashboard

The `DATA_PROTECTION_OFFICER` role has a dashboard to triage privacy requests,
view consent history, monitor retention status and review AI-processing
configuration. Access is itself audited.
