# Architecture

MediConnect AI is structured as a clinic operations platform with a Spring Boot
API, React dashboard, PostgreSQL data store, Redis cache, and optional external
providers for AI and patient notifications.

## Current MVP

- Frontend: React, TypeScript, Vite, CSS, lucide-react icons.
- Backend: Java 21, Spring Boot Web, validation, actuator.
- Data: in-memory demo store exposed through REST endpoints.
- Database: PostgreSQL schema and seed scripts prepared for persistence.
- Infrastructure: Docker Compose with frontend, backend, Postgres, Redis.

## Intended Modules

- Identity and access control.
- Patient registry.
- Appointment scheduling.
- Medical records and prescriptions.
- AI symptom summary assistant.
- Notifications.
- Audit and privacy requests.

## Production Upgrade Path

Move the demo store into real JPA repositories, enforce role-based access,
store audit trails for all clinical access, and make AI/notification providers
configurable through environment variables.
