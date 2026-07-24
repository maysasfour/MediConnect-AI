# AI Safety Design — MediConnect AI Symptom Assistant

The symptom assistant is deliberately narrow: it is an **informational and
care-navigation aid**, not a clinician. This document describes the guardrails
that enforce that boundary, where they live in the code, and how they are tested.

## 1. Non-negotiable safety properties

1. **No diagnosis.** The assistant never asserts that a user has a specific
   disease.
2. **No prescribing.** It never recommends specific medications, doses or
   regimens.
3. **Informational framing.** Every session and response is framed as general
   information plus care navigation.
4. **Emergency interrupt.** Emergency warning signs interrupt the normal chat
   and direct the user to immediate/emergency care.
5. **Doctor review before record.** AI-generated summaries require explicit
   doctor review before they can enter an electronic medical record.
6. **No real patient data in development / testing / seed data.**
7. **No provider exposure without consent** and an approved data-processing
   design.
8. **Data minimization** — direct identifiers are removed from AI requests.

## 2. Request pipeline and where each guard runs

The core orchestration is
[`SymptomTriageService`](backend/src/main/java/com/mediconnect/aiassistant/service/SymptomTriageService.java).
The **ordering is itself a safety property** and is covered by tests.

```
user message
   │
   ▼
[1] Consent gate ───────────────► reject if no consent (ForbiddenOperation)
   │
   ▼
[2] Red-flag detection ─────────► EMERGENCY? interrupt + emergency guidance,
   │  (RedFlagDetector)            and DO NOT call any model
   ▼
[3] Input sanitization ─────────► neutralize prompt injection, cap length,
   │  (PromptSanitizer)            wrap user text as data
   ▼
[4] Model provider (mock/real) ─► de-identified, minimized ModelRequest only
   │  (AiModelProvider)
   ▼
[5] Output validation ──────────► diagnosis/prescription language → safe fallback
   │  (PromptSanitizer)
   ▼
structured, non-diagnostic result (care level, suggested specialty,
follow-ups, neutral summary, assistant message)
```

### [1] Consent gate
No symptom text is processed or stored until the patient has explicitly
consented on the conversation (`AiConversation.grantConsent()`), and the UI shows
a consent screen before the input is even rendered
(`frontend/src/pages/AiAssistantPage.tsx`).

### [2] Red-flag detection (before any model call)
[`RedFlagDetector`](backend/src/main/java/com/mediconnect/aiassistant/service/RedFlagDetector.java)
is a deterministic, bilingual (AR/EN) rule engine covering chest pain, stroke
signs, breathing difficulty, severe bleeding, suicidal ideation / self-harm,
anaphylaxis, loss of consciousness/seizure, obstetric emergencies and
thunderclap headache. It:

- runs **before** the external model, so an emergency message is **never**
  forwarded to a provider;
- **favours recall** (a false negative is far worse than a false positive);
- normalizes case and strips Arabic short-vowel diacritics so vocalization
  variants still match.

On a match, the turn short-circuits to an emergency message with local emergency
guidance (911 in Jordan). The frontend mirrors this client-side purely for
instant feedback (`frontend/src/lib/redFlags.ts`); the **server** check is
authoritative.

### [3] Input sanitization / prompt-injection defense
[`PromptSanitizer.sanitizeInput`](backend/src/main/java/com/mediconnect/aiassistant/service/PromptSanitizer.java)
detects common injection patterns ("ignore previous instructions", "you are
now…", "reveal your system prompt", "developer mode", role-swap attempts), caps
input length, and **wraps** user content in explicit delimiters so the model
treats it as data, not instructions. Detected attempts are logged (without
storing the raw payload beyond the conversation) and do not change the guard
ordering.

### [4] Provider abstraction & data minimization
[`AiModelProvider`](backend/src/main/java/com/mediconnect/aiassistant/provider/AiModelProvider.java)
is a swappable interface; the default
[`MockAiModelProvider`](backend/src/main/java/com/mediconnect/aiassistant/provider/MockAiModelProvider.java)
performs **no external calls**, so no data leaves the system in demos/tests. The
`ModelRequest` carries only sanitized symptom text, a language code and prior
turns — **never** names, national id, MRN, phone or email. Swapping in a real
provider is a matter of adding a bean and setting `mediconnect.ai.provider`;
the safety pipeline around it is unchanged.

### [5] Output validation
[`PromptSanitizer.isOutputSafe`](backend/src/main/java/com/mediconnect/aiassistant/service/PromptSanitizer.java)
rejects model output that looks like a diagnosis ("you have…", "your diagnosis
is…") or prescribing ("take 500 mg…", "dose of…"). Rejected output is replaced
with a safe, information-only fallback in the user's language.

### Care level & structured summary
The result carries a `CareLevel` of `EMERGENCY`, `URGENT_SAME_DAY`,
`ROUTINE_APPOINTMENT` or `GENERAL_INFORMATION`, a suggested specialty (routing
hint only), structured follow-up questions and a neutral symptom summary. The
summary's `DoctorReviewStatus` starts at `PENDING_REVIEW`; only a doctor
accepting it can promote it toward the record. The AI **never** writes to the
record automatically.

## 3. Deletion & data lifecycle

A patient can delete an AI conversation (`AiConversation.softDelete()`), and the
privacy module treats AI conversations as patient data subject to access,
correction and deletion requests (see [`PRIVACY.md`](PRIVACY.md)).

## 4. Tests

Safety behavior is unit-tested (no database required), so the rules can be
verified in isolation and run on every build:

- `RedFlagDetectorTest` — EN + AR emergency phrases flagged; routine messages
  not flagged; empty input safe; case/diacritic normalization.
- `SymptomTriageServiceTest` — consent required; red flag interrupts and the
  model is **never** called; non-emergency reaches the provider;
  diagnosis/prescription output is replaced with a safe fallback.
- `PromptSanitizerTest` — injection detection, user-text wrapping, truncation,
  and diagnosis/prescription output rejection.
- Frontend `AiAssistantPage.test.tsx` + Playwright `assistant.spec.ts` — consent
  gate precedes input; emergency message triggers the assertive emergency banner
  and hides the input.

## 5. Known limitations

- Rule-based red-flag detection cannot catch every emergency phrasing; it is a
  guardrail, not a triage nurse. It is intentionally tuned for recall and should
  be expanded with clinical review before any real deployment.
- The mock provider is deterministic and simplistic; a real provider must be
  evaluated for safety, bias and localization, and only connected under an
  approved data-processing agreement with consent.
- None of this constitutes medical advice or regulatory clearance.
