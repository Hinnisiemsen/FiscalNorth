# Authentication & user isolation

Fiscal North uses **session-cookie authentication** with a BFF-style setup: the Angular dev server proxies API, OAuth, and login routes to the Spring Boot backend. Financial data is scoped per **household** (fully shared between up to 2 members) with an `owner_id` on each record for attribution.

## Sign-in methods

| Method | Endpoint / flow |
|--------|-----------------|
| Email + password | `POST /api/auth/login` |
| Registration | `POST /api/auth/register` |
| Google OAuth 2.0 | `GET /oauth2/authorization/google` |
| Logout | `POST /api/auth/logout` |
| Session status | `GET /api/auth/status` |

Protected routes require an authenticated session. The frontend sends cookies (`withCredentials: true`) and the CSRF token header on mutating requests.

## Demo accounts (seed data)

When running with the default H2 seed (`import.sql`):

| User | Email | Password | Role |
|------|-------|----------|------|
| Alex | `alex@fiscalnorth.local` | `demo1234` | Household owner |
| Jamie | `jamie@fiscalnorth.local` | `demo1234` | Household member |

Both users share one household with accounts, budgets, transactions, goals, and portfolio.

## Household invites

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/household/me` | Current household + members |
| `POST` | `/api/household/invite` | Owner invites partner by email |
| `POST` | `/api/household/invites/accept?token=` | Accept pending invite |

**Join flow (demo):**

1. Alex opens `/household` and sends an invite.
2. Copy the join link shown in settings: `/household/join?token=…`
3. Partner registers or logs in, then opens the join link and accepts.

## Local development

1. Start the backend on port **8080**.
2. Start the frontend with the dev proxy (`npm start` in `frontend/`).
3. Open **http://localhost:4200** (or the port Angular prints).
4. Sign in at `/login` or register a new account.

New users receive **12 default categories** and a **one-person household** automatically via `UserOnboardingService`.

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

## Data scoping

Every entity stores `owner_id` (who created/attributed the record) and, for shared data, `household_id`:

- Accounts, categories, contracts, budgets, transactions, goals, portfolio
- Payment and transfer transactions
- Financial notifications (delivered per user; cron jobs use household totals)

Services filter list endpoints by the authenticated user's `household_id`. Cron jobs iterate households and notify all premium members.

## Frontend integration

| Piece | Role |
|-------|------|
| `AuthService` | Login, register, logout, session status |
| `authInterceptor` | Cookies, CSRF, 401 → `/login`, `Accept-Language` |
| `authGuard` | Protects all layout routes |
| `UserMenuComponent` | Topbar/drawer menu with account settings + sign out |
| `/account` | Profile, locale, password change |
| `/household` | Members, invite partner |
| `/household/join?token=` | Accept household invite |

Proxy entries in `frontend/proxy.conf.json` forward `/api`, `/oauth2`, and `/login` to the backend.

## Security notes

- Passwords are stored as BCrypt hashes (`passwordHash` is never serialized to JSON).
- Sessions are persisted with JDBC (`spring.session.store-type=jdbc`).
- CSRF uses a cookie repository; the frontend reads `XSRF-TOKEN` and sends `X-XSRF-TOKEN`.
- OAuth users are provisioned on first login; linking by email upgrades `authProvider` to `BOTH` when applicable.

## Mobile clients (iOS)

The native iOS app in [`ios/`](../ios/) uses the same session-cookie + CSRF model as the Angular SPA, but calls the Spring Boot API **directly** (e.g. `http://localhost:8080/api/...` in Simulator). Browser CORS rules do not apply to native URLSession clients.

| Step | Endpoint / behavior |
|------|---------------------|
| CSRF bootstrap | `GET /api/auth/csrf` before the first mutating request |
| Login | `POST /api/auth/login` with JSON body + CSRF header |
| Session | `JSESSIONID` cookie stored in `HTTPCookieStorage` |
| Mutations | Include CSRF header on POST/PUT/DELETE |
| Status check | `GET /api/auth/status` on app launch |

**Simulator:** point the iOS client at `http://localhost:8080`.

**Physical device:** use your Mac's LAN IP (e.g. `http://192.168.1.10:8080`) because `localhost` on the device refers to the phone itself.

Google OAuth is not implemented in the iOS MVP; use email/password or register a new account.

See [ios/README.md](../ios/README.md) for setup and verification steps.

## Testing

Backend tests use dummy OAuth client credentials via `application-test.properties`. Run:

```bash
cd backend && ./mvnw test
```
