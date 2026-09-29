# Requirements

## MVP Requirements

- Clinician can view dashboard metrics.
- Clinician can view patients and risk levels.
- Clinician can inspect a patient's recent medical records.
- Clinician can view and request appointments.
- Clinician can generate a safe AI symptom summary.
- App can run locally with separate backend and frontend commands.
- App can run with Docker Compose.

## Non-Functional Requirements

- Healthcare AI output must be framed as informational support, not diagnosis.
- Patient data access should be audited before production use.
- Secrets must come from environment variables.
- Production auth must use hashed passwords and signed tokens.
