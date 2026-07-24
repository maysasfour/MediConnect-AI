# Demo Credentials

> **Fictional accounts only.** No real users or patients. For demo/testing use.

All demo accounts share the password:

```
Demo1234!Pass
```

The seed stores a BCrypt(strength 12) hash of this password
(`backend/src/main/resources/db/migration/V2__seed_demo_data.sql`).

| Role | Email | Clinic |
|------|-------|--------|
| System admin | `sysadmin@demo.mediconnect.local` | (platform-wide, no clinic) |
| Clinic admin | `admin@demo.mediconnect.local` | Amman Family Care Center |
| Doctor | `doctor@demo.mediconnect.local` | Amman Family Care Center |
| Receptionist | `reception@demo.mediconnect.local` | Amman Family Care Center |
| Patient | `patient@demo.mediconnect.local` | Amman Family Care Center |

Demo clinic: **Amman Family Care Center** (`aaaaaaaa-0000-0000-0000-000000000001`),
Abdoun branch. Two fictional patients are seeded (`MRN-0001`, `MRN-0002`).

## Log in via API

```bash
curl -sX POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"doctor@demo.mediconnect.local","password":"Demo1234!Pass"}'
```

Use the returned `accessToken` as `Authorization: Bearer <token>` for protected
endpoints.

> Change or remove these accounts before any non-demo use.
