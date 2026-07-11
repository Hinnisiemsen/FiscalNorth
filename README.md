# FiscalNorth

Modern personal finance app built as a **portfolio showcase project**: Spring Boot backend + Angular frontend with household collaboration, shared budgets, investment tracking, and AI-assisted insights.

> Single-user PF core → 2-adult household MVP → shared portfolio. See [ROADMAP.md](ROADMAP.md) for product decisions and phase status.

## Current state

| Area | Status |
|------|--------|
| Core PF (accounts, transactions, budgets, contracts, goals) | Done |
| Split transactions | Done — API + UI, budget/insights aware |
| Demo paywall preview | Done — "Try in demo" without Stripe |
| Household (2 adults, fully shared) | Done — invite + join flow |
| Shared budgets | Done — remaining column, per-member breakdown |
| Shared portfolio | Done — holdings, allocation chart, net-worth KPI |
| Live prices | Alpha Vantage + cached fallback + daily cron |
| Demo seed | Alex + Jamie household, split tx, sample holdings |
| E2E tests | Cypress — auth, transactions, billing, household, portfolio |
| iOS app (SwiftUI MVP) | In progress — see [ios/README.md](ios/README.md) |

## Quick demo

Start locally (see [Getting started](#getting-started)), then sign in as **Alex**:

| | |
|---|---|
| Email | `alex@fiscalnorth.local` |
| Password | `demo1234` |

Partner account **Jamie**: `jamie@fiscalnorth.local` / `demo1234` (same household, shared data).

**Routes worth trying**

| Route | What you'll see |
|-------|-----------------|
| `/` | Dashboard — net worth, household budgets, category drill-down |
| `/budgets` | Remaining amounts + Alex/Jamie spend breakdown |
| `/portfolio` | AAPL + VWCE.DE holdings, asset allocation |
| `/household` | Members, invite partner, join link |
| `/transactions` | Split transaction "Kaufland Wocheneinkauf" |
| `/assistant` | Premium paywall → click **Try in demo** to unlock AI |

Premium features work locally via `DEMO_PREMIUM_PREVIEW=true` (default). Set `GEMINI_API_KEY` for the AI assistant.

## Features

### Personal finance
- **Accounts** — Deposit, bank (13 account types), and crypto accounts
- **Transactions** — Income, expenses, transfers, CSV import, **split bookings**
- **Contracts** — Recurring payments, auto-detection, optional Gemini analysis (text/PDF)
- **Budgets** — Limits with usage, remaining, and per-member attribution
- **Goals** — Savings targets with AI-powered planning interview
- **Insights** — Monthly trends and category breakdown (split-aware)

### Household & portfolio
- **Household** — Max 2 adults (owner + member), fully shared data
- **Invites** — Owner invites by email; partner joins via `/household/join?token=…`
- **Portfolio** — Shared holdings, cost basis, unrealized gain, allocation chart
- **Prices** — Alpha Vantage live quotes with cached fallback (`manual-fallback` default)

### Premium & platform
- **AI assistant** — Google Gemini for financial Q&A and actions
- **Bank sync (XS2A)** — PSD2 via finAPI sandbox (Premium)
- **Stripe billing** — Subscription gating with demo preview mode
- **Platform extras** — Crypto accounts, admin API, WebSocket events ([docs/PLATFORM.md](docs/PLATFORM.md))

## Project structure

```
FiscalNorth/
├── backend/              # Spring Boot API (Java 21)
│   └── lib/              # finAPI XS2A client JAR
├── frontend/             # Angular 20 SPA
├── ios/                  # Native SwiftUI iOS app (see ios/README.md)
├── docs/                 # AUTH, BILLING, DEPLOY, STAGING, PLATFORM
├── ROADMAP.md            # Product roadmap & status
└── compose.yaml          # Docker Compose stack
```

## Tech stack

| Layer | Technologies |
|-------|--------------|
| Backend | Java 21, Spring Boot 3.3, Spring Data JPA, Spring Security |
| Database | PostgreSQL (production), H2 (local dev) |
| AI | Google Gemini via Spring AI |
| Frontend | Angular 20, TypeScript |
| Bank sync | Berlin Group XS2A (finAPI client JAR) |
| Messaging | RabbitMQ, Kafka (infra; optional) |

## Getting started

### Prerequisites

- Java 21
- Node.js & npm
- Docker & Docker Compose

### Option 1 — Docker Compose (full stack)

```bash
git clone https://github.com/Hinnisiemsen/FiscalNorth
cd FiscalNorth

# Optional: copy .env.example → .env and set GEMINI_API_KEY
docker compose up -d --build
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |

### Option 2 — Local development

**1. Install XS2A client JAR (one-time):**

```bash
cd backend
./mvnw install:install-file \
  -Dfile=lib/openapi-java-client-1.3.14_2025-01-24.jar \
  -DgroupId=org.openapitools -DartifactId=openapi-java-client \
  -Dversion=1.3.14_2025-01-24 -Dpackaging=jar
```

**2. Start infrastructure (optional):**

```bash
docker compose up -d postgres rabbitmq
```

**3. Backend:**

```bash
cd backend
./mvnw spring-boot:run
```

**4. Frontend:**

```bash
cd frontend
npm install
npm start
```

Open http://localhost:4200 and sign in with the demo credentials above.

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `GEMINI_API_KEY` | — | Google Gemini API key (AI assistant, goal planner, contract analysis) |
| `DEMO_PREMIUM_PREVIEW` | `true` | Unlock premium features without Stripe |
| `PORTFOLIO_PRICE_PROVIDER` | `manual-fallback` | `alphavantage` for live quotes |
| `PORTFOLIO_PRICE_API_KEY` | — | Alpha Vantage API key |
| `STRIPE_ENABLED` | `false` | Enable Stripe billing |
| `XS2A_ENABLED` | `false` | Enable bank sync |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | — | Google OAuth |

See `backend/src/main/resources/application.properties` and `.env.example` for the full list.

## API overview

| Area | Endpoints |
|------|-----------|
| Accounts | `GET/POST /api/account/deposit`, `/api/account/bank`, `/api/account/crypto` |
| Transactions | `GET/POST /api/transaction/payment`, `GET/PUT /api/transaction/payment/{id}/splits` |
| CSV import | `POST /api/transaction/import/csv` |
| Insights | `GET /api/transaction/insights` |
| Contracts | `GET/POST /api/contract`, `POST /api/contract/analyze/document` |
| Budgets | `GET/POST /api/budget`, `GET /api/budget/with-usage` |
| Household | `GET /api/household/me`, `POST /api/household/invite`, `POST /api/household/invites/accept?token=` |
| Portfolio | `GET /api/portfolio`, `POST /api/portfolio/holdings` |
| Goals | `GET/POST /api/goal`, `POST /api/goal/interview` |
| Bank sync | `GET /api/bank-sync/status`, `POST /api/bank-sync/consent` |
| Billing | `GET /api/billing/subscription`, `GET /api/billing/plans` |

## Testing

```bash
cd backend && ./mvnw test          # 39 unit/integration tests
cd frontend && npm run build        # production build
cd frontend && npm run e2e:ci       # Cypress E2E (requires running stack)
```

## CI/CD

| Workflow | Trigger | Description |
|----------|---------|-------------|
| CI | Push/PR to master | Backend tests, frontend build, Cypress, Compose smoke |
| Docker Build | Push/PR, release | Backend + frontend images → ghcr.io |
| Deploy | Post-build on master | SSH deploy via `compose.prod.yaml` |
| Lint | PR (frontend/) | Prettier check |

## Documentation

| Doc | Contents |
|-----|----------|
| [ROADMAP.md](ROADMAP.md) | Product roadmap and phase status |
| [docs/AUTH.md](docs/AUTH.md) | Auth, OAuth, demo accounts, household invites |
| [docs/BILLING.md](docs/BILLING.md) | Stripe premium setup |
| [docs/STAGING.md](docs/STAGING.md) | Staging checklist, demo preview mode |
| [docs/PLATFORM.md](docs/PLATFORM.md) | Crypto, admin API, WebSocket |
| [docs/DEPLOY.md](docs/DEPLOY.md) | Production deployment |

---

**Author:** Hinni Siemsen
