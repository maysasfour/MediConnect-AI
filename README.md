# MediConnect AI

MediConnect AI is a healthcare management and AI-assisted care-navigation
platform designed for clinics and medical centers.

This repository contains a complete runnable website demo: a Spring Boot REST
API, separate React/Vite portals for doctors, patients, and administrators,
seeded database scripts, and Docker wiring.
The backend serves seeded in-memory data so the complete workflow can be
shown immediately without requiring external infrastructure.

Live website: https://maysasfour.github.io/MediConnect-AI/

## Main Features

- Patient registration
- Doctor dashboard
- Appointment booking
- Electronic medical records
- Prescription history
- Notification reminder previews
- AI symptom-information assistant
- Analytics dashboard
- Role-based access control
- Responsive desktop and mobile navigation
- Consultation and prescription workflows
- User administration and audit views

## Technology Stack

### Backend
- Java 21
- Spring Boot
- PostgreSQL
- Redis

### Frontend
- React
- TypeScript
- Vite
- Lucide icons
- Custom responsive CSS design system

### Infrastructure
- Docker
- Docker Compose
- Nginx
- GitHub Actions

## Project Status

Runnable website complete. The UI includes authentication, role-aware routing,
doctor workflows, a patient self-service portal, administration, analytics,
audit views, resilient demo fallbacks, and live API integration. PostgreSQL,
Redis, external notification delivery, and a hosted AI provider remain optional
production integrations.

## Quick Start

### Backend

```bash
cd backend
mvn spring-boot:run
```

API health check:

```bash
curl http://localhost:8080/api/health
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

Use one of these demo website accounts:

- Doctor: `doctor@mediconnect.ai` / `doctor123`
- Patient: `patient@mediconnect.ai` / `patient123`
- Administrator: `admin@mediconnect.ai` / `admin123`

### Docker

```bash
docker compose up --build
```

Open `http://localhost:3000`.

## API Endpoints

- `GET /api/health`
- `POST /api/auth/login`
- `GET /api/patients`
- `GET /api/patients/{id}`
- `GET /api/appointments`
- `POST /api/appointments`
- `GET /api/patients/{id}/records`
- `GET /api/doctors`
- `GET /api/analytics/summary`
- `POST /api/ai/triage-summary`

Versioned module endpoints are available under `/api/v1`, including:

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `GET|POST|PUT|DELETE /api/v1/patients`
- `GET|POST /api/v1/appointments`
- `GET|POST /api/v1/medical-records`
- `GET|POST /api/v1/prescriptions`
- `GET /api/v1/clinics`
- `POST /api/v1/ai/symptom-summary`

## Finish Roadmap

1. Replace in-memory stores with Spring Data JPA repositories.
2. Enforce route authorization with Spring Security.
3. Connect the provided schema and seed scripts to PostgreSQL.
4. Replace provider interfaces with production SMS, WhatsApp, and AI adapters.
5. Add comprehensive unit, integration, and frontend component test suites.
