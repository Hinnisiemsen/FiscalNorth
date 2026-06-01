# Authentication & user isolation

Fiscal North uses **session-cookie authentication** with a BFF-style setup: the Angular dev server proxies API, OAuth, and login routes to the Spring Boot backend. All financial data is scoped per user via an `owner_id` foreign key.

## Sign-in methods

| Method | Endpoint / flow |
|--------|-----------------|
| Email + password | `POST /api/auth/login` |
| Registration | `POST /api/auth/register` |
| Google OAuth 2.0 | `GET /oauth2/authorization/google` |
| Logout | `POST /api/auth/logout` |
| Session status | `GET /api/auth/status` |

Protected routes require an authenticated session. The frontend sends cookies (`withCredentials: true`) and the CSRF token header on mutating requests.

## Demo account (seed data)

When running with the default H2 seed (`import.sql`):

| Field | Value |
|-------|--------|
| Email | `alex@fiscalnorth.local` |
| Password | `demo1234` |
| Role | `USER` |

## Local development

1. Start the backend on port **8080**.
2. Start the frontend with the dev proxy (`npm start` in `frontend/`).
3. Open **http://localhost:4200** (or the port Angular prints).
4. Sign in at `/login` or register a new account.

New users receive **12 default categories** automatically via `UserOnboardingService`.

## Google OAuth setup

1. Create OAuth 2.0 credentials in [Google Cloud Console](https://console.cloud.google.com/).
2. Add authorized redirect URI:

   ```
   http://localhost:8080/login/oauth2/code/google
   ```

3. Copy `application-local.properties.example` to `application-local.properties` (gitignored) or set environment variables:

   ```bash
   GOOGLE_CLIENT_ID=your-client-id
   GOOGLE_CLIENT_SECRET=your-client-secret
   ```

4. Restart the backend. The **Continue with Google** button on `/login` redirects through Spring Security OAuth2.

If credentials are missing, local login still works; Google sign-in will fail at redirect time.

## Profile & account API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/user/me` | Current user profile |
| `PUT` | `/api/user/me` | Update display name, locale, avatar URL |
| `PUT` | `/api/user/me/password` | Change password (local accounts) |
| `GET` | `/api/user` | List all users (**ADMIN** only) |

## Data isolation

Every user-owned entity stores `owner_id` referencing `app_user`:

- Accounts, categories, contracts, budgets
- Payment and transfer transactions
- Financial notifications
- Bank consents (XS2A)

Services and repositories filter by the authenticated user's ID. Cron jobs iterate users independently so notifications and AI jobs never leak across tenants.

## Frontend integration

| Piece | Role |
|-------|------|
| `AuthService` | Login, register, logout, session status |
| `authInterceptor` | Cookies, CSRF, 401 → `/login`, `Accept-Language` |
| `authGuard` | Protects all layout routes |
| `UserMenuComponent` | Topbar/drawer menu with account settings + sign out |
| `/account` | Profile, locale, password change |

Proxy entries in `frontend/proxy.conf.json` forward `/api`, `/oauth2`, and `/login` to the backend.

## Security notes

- Passwords are stored as BCrypt hashes (`passwordHash` is never serialized to JSON).
- Sessions are persisted with JDBC (`spring.session.store-type=jdbc`).
- CSRF uses a cookie repository; the frontend reads `XSRF-TOKEN` and sends `X-XSRF-TOKEN`.
- OAuth users are provisioned on first login; linking by email upgrades `authProvider` to `BOTH` when applicable.

## Testing

Backend tests use dummy OAuth client credentials via `application-test.properties`. Run:

```bash
./backend/mvnw -f pom.xml -pl backend test
```
