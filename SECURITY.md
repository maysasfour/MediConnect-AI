# Security

The current app is a demo MVP. It is not production hardened yet.

## Before Production

- Replace demo login with real authentication.
- Hash passwords with a modern password hashing algorithm.
- Add JWT signing and refresh-token rotation.
- Add role-based authorization on every clinical endpoint.
- Add rate limits for auth and AI endpoints.
- Add dependency and container scanning.
- Add HTTPS and secure headers at the edge.
