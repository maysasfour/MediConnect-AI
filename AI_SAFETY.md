# AI Safety

The MVP AI assistant is intentionally conservative and demo-only.

## Rules

- Do not present AI output as a diagnosis.
- Include escalation language for severe or worsening symptoms.
- Keep the clinician in control of interpretation and care decisions.
- Log AI interactions before production use.
- Avoid sending unnecessary personal health information to external providers.

## Production Checklist

- Add provider-level safety configuration.
- Add prompt and response audit logs.
- Add red-team test cases for emergency symptoms.
- Add disclaimers in the UI wherever AI output appears.
