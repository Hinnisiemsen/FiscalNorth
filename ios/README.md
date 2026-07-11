# FiscalNorth iOS App

Native SwiftUI client for the FiscalNorth personal finance API. This is a **showcase MVP** with read-only views for dashboard, accounts, transactions, budgets, and portfolio.

## Prerequisites

- macOS with **Xcode 15+**
- [XcodeGen](https://github.com/yonaskolb/XcodeGen) (`brew install xcodegen`)
- FiscalNorth backend running locally (Docker Compose or Spring Boot)

## Quick start

### 1. Start the backend

From the repo root:

```bash
docker compose up -d
# or: cd backend && ./mvnw spring-boot:run
```

The API listens on **http://localhost:8080**.

### 2. Generate the Xcode project

```bash
cd ios
xcodegen generate
open FiscalNorth.xcodeproj
```

### 3. Run in Simulator

1. Select an iPhone simulator (iOS 17+).
2. Build and run (**Cmd+R**).
3. Sign in with demo credentials:

| Email | Password |
|-------|----------|
| `alex@fiscalnorth.local` | `demo1234` |

Jamie (`jamie@fiscalnorth.local` / `demo1234`) shares the same household data.

## Architecture

```
FiscalNorth/
├── App/              # App entry + root navigation
├── Core/
│   ├── Config/       # API base URL (Debug → localhost:8080)
│   ├── Network/      # URLSession client with session cookies + CSRF
│   ├── Models/       # Codable DTOs matching the REST API
│   ├── Services/     # Auth, accounts, transactions, budgets, portfolio
│   └── Utilities/    # Currency and date formatters
└── Features/         # SwiftUI screens (Login, Dashboard, tabs)
```

Authentication mirrors the Angular SPA:

1. `GET /api/auth/csrf` — bootstrap CSRF token
2. `POST /api/auth/login` — establish session (`JSESSIONID` cookie)
3. Mutating requests include the `X-XSRF-TOKEN` header

Native apps call the backend **directly** (no nginx proxy). CORS does not apply to URLSession.

## Configuration

| Build | API base URL |
|-------|--------------|
| Debug | `http://localhost:8080` |
| Release | `FISCALNORTH_API_URL` env var, or `https://api.fiscalnorth.example` |

### Physical device testing

The Simulator can reach `localhost:8080` on your Mac. A physical iPhone cannot — use your Mac's LAN IP instead:

1. Find your IP: `ipconfig getifaddr en0`
2. Temporarily change `APIConfiguration.swift` Debug URL to `http://192.168.x.x:8080`
3. Ensure backend binds to `0.0.0.0` (Docker Compose default)

App Transport Security allows local networking in Debug via `NSAllowsLocalNetworking` in `project.yml`.

## MVP scope

**Included**

- Email/password login and registration
- Dashboard (net worth, cash, portfolio, budget KPIs)
- Accounts list (deposit, bank, crypto)
- Transactions list
- Budgets with usage and member breakdown
- Portfolio holdings

**Deferred**

- Google OAuth (`ASWebAuthenticationSession`)
- AI assistant, billing, bank sync
- Create/edit flows (use web app)
- Push notifications, offline cache

## Manual verification checklist

- [ ] Backend running on port 8080
- [ ] Login as Alex succeeds
- [ ] Dashboard shows net worth and budget KPIs
- [ ] Accounts tab lists household accounts
- [ ] Transactions tab shows payment history
- [ ] Budgets tab shows remaining amounts
- [ ] Portfolio tab shows AAPL / VWCE.DE holdings
- [ ] Sign out returns to login screen
- [ ] Pull-to-refresh reloads data on each tab

## CI note

GitHub Actions CI runs on Linux and does not compile this Xcode project. Build verification is manual on macOS. A future `macos-latest` workflow can run `xcodebuild` if desired.

## Related docs

- [AUTH.md](../docs/AUTH.md) — authentication details + mobile client notes
- [README.md](../README.md) — full stack setup
- [ROADMAP.md](../ROADMAP.md) — Phase 6 iOS status
