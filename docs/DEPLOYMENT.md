# Deployment Guide — MediConnect AI

## 1. Environments & configuration

All configuration is environment-based. **Never commit real secrets.**

### Backend environment variables

| Variable | Default (demo) | Purpose |
|----------|----------------|---------|
| `DB_URL` | `jdbc:postgresql://db:5432/mediconnect` | JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | `mediconnect` | DB credentials |
| `REDIS_HOST` / `REDIS_PORT` | `redis` / `6379` | Redis |
| `JWT_SECRET` | demo value | **≥32 bytes**; rotate for prod |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:8081` | comma list |
| `AI_PROVIDER` | `mock` | AI provider selector |

### Frontend build-time

| Variable | Purpose |
|----------|---------|
| `VITE_API_URL` | Backend origin for the dev proxy |

## 2. Local / demo via Docker Compose

```bash
docker compose up --build      # postgres, redis, backend, frontend
```

- Frontend: `http://localhost:8081`
- Backend/Swagger: `http://localhost:8080/swagger-ui.html`

Flyway runs migrations (schema + fictional seed) on backend start. Healthchecks
gate service readiness; the backend waits for a healthy DB and Redis.

Override demo secrets with an untracked `.env` at repo root, e.g.:

```env
JWT_SECRET=please-generate-a-long-random-value-min-32-bytes
DB_PASSWORD=another-strong-secret
CORS_ALLOWED_ORIGINS=https://clinic.example.jo
```

## 3. Production considerations

1. **Secrets** from a manager (AWS Secrets Manager, Vault, k8s Secrets), not
   env files. Rotate `JWT_SECRET` and DB credentials.
2. **TLS everywhere.** Terminate TLS at the ingress/load balancer; set
   `server.forward-headers-strategy` (already `framework`) and HSTS at the edge.
3. **Database.** Managed PostgreSQL 16+, automated backups, PITR, least-priv
   app user. Run Flyway on deploy (it runs on startup by default; for
   zero-downtime, gate migrations in a release step).
4. **Redis** for rate limiting / idempotency / short-lived state; enable auth.
5. **Scaling.** The backend is stateless (JWT); scale horizontally behind a load
   balancer. Refresh tokens and idempotency live in the DB/Redis, not memory.
6. **Observability.** Ship JSON logs to a central store keyed by
   `correlationId`; scrape `/actuator` metrics; alert on health probes.
7. **CORS** restricted to real frontend origins; no wildcard with credentials.
8. **AI provider.** Keep `AI_PROVIDER=mock` unless an external provider is
   contracted with a DPA and patient consent; then supply provider credentials
   via secrets and keep data minimization on.

## 4. Container images

- `backend/Dockerfile` — multi-stage Maven build → JRE 21 runtime, non-root
  user, healthcheck on `/actuator/health/liveness`.
- `frontend/Dockerfile` — Vite build → Nginx serving static assets + `/api`
  proxy, with security headers (`frontend/nginx.conf`).

## 5. CI/CD

`.github/workflows/ci.yml` builds and tests backend and frontend, runs Playwright
e2e, scans dependencies (Trivy + `npm audit`) and runs CodeQL. Extend with a
deploy job (image build/push + environment rollout) per your target platform.

## 6. Kubernetes (sketch)

Deployments for backend (with liveness/readiness probes at
`/actuator/health/liveness|readiness`) and frontend; a Service + Ingress with
TLS; External Secrets for config; a managed PostgreSQL and Redis. HPA on the
backend by CPU/latency. Run migrations as an init container or a pre-deploy Job.
