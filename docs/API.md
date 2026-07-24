# API Reference — MediConnect AI

Base path: **`/api/v1`**. Interactive docs (when the backend runs):
`http://localhost:8080/swagger-ui.html`; raw OpenAPI: `/v3/api-docs`.

## Conventions

- **Auth:** `Authorization: Bearer <accessToken>` on all non-public endpoints.
- **Content type:** `application/json`; errors are `application/problem+json`
  (RFC 7807).
- **Correlation:** send/receive `X-Correlation-Id`; echoed on responses and in
  error bodies.
- **Idempotency:** send `Idempotency-Key` on appointment creation and external
  notification sends; a repeated key returns the original result.
- **Pagination:** `?page=0&size=20&sort=field,asc`; responses include page
  metadata.
- **Filtering/search:** endpoint-specific query params (e.g. `?nationalId=`,
  `?from=&to=`).
- **Localization:** `Accept-Language: ar` or `en`.

## Error format (RFC 7807)

```json
{
  "type": "https://mediconnect.example/problems/business-rule",
  "title": "Business rule violated",
  "status": 422,
  "detail": "Cannot move appointment from REQUESTED to COMPLETED",
  "instance": "/api/v1/appointments/.../status",
  "code": "ILLEGAL_STATUS_TRANSITION",
  "correlationId": "1f2e...",
  "timestamp": "2026-07-24T09:00:00Z"
}
```

Status codes: `400` validation/malformed, `401` unauthenticated, `403`
forbidden, `404` not found / cross-tenant, `409` conflict / optimistic-lock /
double booking, `422` business-rule violation, `500` unexpected.

## Authentication

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/register` | public | Register a patient account |
| POST | `/auth/login` | public | Obtain access + refresh tokens |
| POST | `/auth/refresh` | public | Rotate refresh token → new pair |
| POST | `/auth/logout` | bearer | Revoke all refresh tokens for the user |

### Examples

```bash
# Login
curl -sX POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"doctor@demo.mediconnect.local","password":"Demo1234!Pass"}'

# Response
{ "accessToken":"...", "refreshToken":"...", "tokenType":"Bearer",
  "expiresInSeconds":900 }

# Rotate
curl -sX POST http://localhost:8080/api/v1/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<raw-refresh-token>"}'
```

## Representative resource endpoints (design)

These follow the same conventions; those marked ✅ are implemented in this repo,
others are designed and on the roadmap.

| Method | Path | Notes |
|--------|------|-------|
| POST | `/auth/login` \| `/auth/register` \| `/auth/refresh` \| `/auth/logout` | ✅ |
| GET | `/patients?page=&size=&sort=` | list (clinic-scoped) |
| POST | `/patients` | create patient; duplicate detection |
| GET | `/patients/{id}` | tenant-checked fetch |
| GET | `/doctors/{id}/availability?date=` | free slots |
| POST | `/appointments` | `Idempotency-Key`; double-booking safe |
| POST | `/appointments/{id}/status` | validated status transition |
| POST | `/encounters/{id}/notes` | create draft note |
| POST | `/notes/{id}/sign` | sign (immutable) |
| POST | `/notes/{id}/amendments` | append-only correction |
| POST | `/prescriptions` | prescription + items |
| POST | `/ai/conversations` | start (consent required) |
| POST | `/ai/conversations/{id}/messages` | triage turn (red-flag guarded) |
| DELETE | `/ai/conversations/{id}` | delete conversation |
| GET | `/analytics/summary?from=&to=&branchId=` | KPIs |
| POST | `/privacy/requests` | data-subject request |

## Health

`GET /actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness`.
