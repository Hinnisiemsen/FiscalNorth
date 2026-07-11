# 🏠 FiscalNorth

Ein modernes Finanz-Management-System bestehend aus einem Spring-Boot-Backend und einer Angular-Frontend-Anwendung. Die App ermöglicht das Management von Bankkonten, Budgets, Verträgen und Transaktionen – inklusive Bank-Sync über Berlin Group XS2A (PSD2) und KI-gestützte Analysefunktionen.

## 📦 Projektstruktur

```
fiscalNorth/
├── backend/              # Spring Boot API (Java 21)
│   └── lib/              # finAPI XS2A client JAR (local Maven artifact)
├── frontend/             # Angular 20 SPA
├── docs/                 # AUTH, BILLING, DEPLOY, STAGING
└── compose.yaml          # Docker Compose (PostgreSQL, RabbitMQ, Backend, Frontend)
```

## 🚀 Features

### Kernfunktionen
* **Kontenverwaltung** – Festgeld/Sparkonten (Deposit) und Bankkonten (Giro, PayPal, Depot, Krypto, …) mit 13 Kontotypen
* **Transaktionsmanagement** – Ausgaben, Einnahmen, Umbuchungen, Kategorisierung, **Split-Buchungen**
* **Vertragsmanagement** – Wiederkehrende Zahlungen; automatische Erkennung + optionale KI-Analyse
* **Budgetierung** – Ausgabenlimits pro Zeitraum inkl. Nutzungsanzeige, Restbetrag und Haushalts-Aufschlüsselung
* **Haushalt** – 2-Erwachsene-MVP, geteilte Daten, Einladungs- und Beitrittsflow
* **Portfolio** – Gemeinsame Holdings mit Live-/Cache-Kursen (Alpha Vantage)
* **Kategorien** – Eigene Kategorien für Transaktionen

### Erweiterte Funktionen
* **Bank-Sync (XS2A)** – PSD2-konforme Anbindung an Banken über finAPI Sandbox (Premium)
* **CSV-Import** – Transaktionen aus CSV-Dateien importieren
* **Insights** – Monatliche Trends und Ausgaben nach Kategorien
* **KI-Integration** – Google Gemini (Assistent, Zielplaner, Vertragsanalyse aus Text/PDF)
* **Premium / Stripe** – Abonnement für KI, Bank-Sync und proactive Notifications

## 🛠 Tech Stack

| Komponente | Technologien |
|------------|--------------|
| **Backend** | Java 21, Spring Boot 3.3.5, Spring Data JPA, Spring Security |
| **API** | Spring MVC REST |
| **Datenbank** | PostgreSQL (Produktion), H2 (lokale Entwicklung) |
| **Messaging** | RabbitMQ, Apache Kafka (infra; optional platform extension) |
| **AI** | Google Gemini via Spring AI |
| **Frontend** | Angular 20, TypeScript |
| **XS2A** | Berlin Group NextGenPSD2 (finAPI client JAR in `backend/lib/`) |

## ⚙️ Voraussetzungen

* Java 21 SDK
* Node.js & npm (für Frontend-Entwicklung)
* Docker & Docker Compose

## 🏃‍♂️ Starten der Anwendung

### Option 1: Vollständiger Stack mit Docker Compose

```bash
git clone https://github.com/Hinnisiemsen/FiscalNorth
cd FiscalNorth

# Optional: copy .env.example to .env and set GEMINI_API_KEY (and OAuth vars if needed)
docker compose up -d --build
```

| Service | URL |
|---------|-----|
| Frontend (SPA + API proxy) | http://localhost:3000 |
| Backend API (direct) | http://localhost:8080 |
| H2 Console (nur bei lokaler H2-Entwicklung) | http://localhost:8080/h2-console |

### Option 2: Lokale Entwicklung

**1. XS2A client JAR installieren (einmalig):**
```bash
cd backend
./mvnw install:install-file \
  -Dfile=lib/openapi-java-client-1.3.14_2025-01-24.jar \
  -DgroupId=org.openapitools -DartifactId=openapi-java-client \
  -Dversion=1.3.14_2025-01-24 -Dpackaging=jar
```

**2. Infrastruktur starten (optional PostgreSQL, RabbitMQ):**
```bash
docker compose up -d postgres rabbitmq
```

**3. Backend starten:**
```bash
cd backend
./mvnw spring-boot:run
```

**4. Frontend starten:**
```bash
cd frontend
npm install
npm start
```

Frontend: http://localhost:4200

**Hinweis:** Lokal nutzt das Backend standardmäßig H2. Demo-Logins (siehe [docs/AUTH.md](docs/AUTH.md)):

| User | Email | Password |
|------|-------|----------|
| Alex (owner) | `alex@fiscalnorth.local` | `demo1234` |
| Jamie (member) | `jamie@fiscalnorth.local` | `demo1234` |

Portfolio-Kurse: `PORTFOLIO_PRICE_API_KEY` setzen und `PORTFOLIO_PRICE_PROVIDER=alphavantage`, sonst Cached Quotes aus dem Seed.

## 📚 API Übersicht

| Bereich | Endpunkte |
|---------|-----------|
| **Bankkonten** | `GET/POST /api/account/bank`, `GET /api/account/bank/{id}` |
| **Festgeld** | `GET/POST /api/account/deposit`, `GET/DELETE /api/account/deposit/{id}` |
| **Transaktionen** | `GET/POST /api/transaction/payment`, `GET/PUT /api/transaction/payment/{id}/splits` |
| **CSV-Import** | `POST /api/transaction/import/csv` |
| **Insights** | `GET /api/transaction/insights` |
| **Verträge** | `GET/POST /api/contract`, `POST /api/contract/analyze`, `POST /api/contract/analyze/document` |
| **Budgets** | `GET/POST /api/budget`, `GET /api/budget/with-usage` |
| **Haushalt** | `GET /api/household/me`, `POST /api/household/invite`, `POST /api/household/invites/accept?token=` |
| **Portfolio** | `GET /api/portfolio`, `POST /api/portfolio/holdings` |
| **Kategorien** | `GET/POST/DELETE /api/category` |
| **Bank-Sync** | `GET /api/bank-sync/status`, `POST /api/bank-sync/consent`, `POST /api/bank-sync/sync` |
| **Billing** | `GET /api/billing/subscription`, `GET /api/billing/plans` |

## 🧪 Testen

```bash
cd backend && ./mvnw test
cd frontend && npm test
cd frontend && npm run e2e:ci
```

## 🔄 CI/CD (GitHub Actions)

| Workflow | Trigger | Beschreibung |
|----------|---------|--------------|
| **CI** | Push/PR auf main/master | Backend tests, frontend build/tests, Cypress E2E, Docker Compose smoke |
| **Deploy** | Nach Docker Build auf master (wenn `DEPLOY_ENABLED=true`) | SSH-Deploy auf VPS via `compose.prod.yaml` |
| **Docker Build** | Push/PR, Release | Baut Backend- und Frontend-Images, push zu ghcr.io |
| **Lint** | PR (frontend/) | Prettier-Check |

## 📝 Konfiguration

| Datei | Beschreibung |
|-------|--------------|
| `backend/src/main/resources/application.properties` | Hauptkonfiguration |
| `.env.example` | Docker Compose Umgebungsvariablen |
| `compose.yaml` | Docker-Services |

### Wichtige Einstellungen
* `GEMINI_API_KEY` – Google Gemini API-Key
* `DEMO_PREMIUM_PREVIEW` – Premium-Features ohne Stripe (Standard: `true` lokal)
* `PORTFOLIO_PRICE_API_KEY` / `PORTFOLIO_PRICE_PROVIDER` – Live-Kurse (Standard: `manual-fallback`)
* `STRIPE_ENABLED` – Stripe Billing aktivieren
* `XS2A_ENABLED` / `app.xs2a.enabled` – Bank-Sync aktivieren
* `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` – OAuth

## 📖 Weitere Dokumentation

* [ROADMAP.md](ROADMAP.md) – Produkt-Roadmap und Status
* [docs/AUTH.md](docs/AUTH.md) – Authentication, OAuth, demo login
* [docs/BILLING.md](docs/BILLING.md) – Stripe Premium setup
* [docs/STAGING.md](docs/STAGING.md) – Staging / premium activation checklist
* [docs/PLATFORM.md](docs/PLATFORM.md) – Crypto accounts, admin API, WebSocket/events
* [docs/DEPLOY.md](docs/DEPLOY.md) – Production deployment
* [backend/lib/README.md](backend/lib/README.md) – XS2A client JAR
* [frontend/README.md](frontend/README.md) – Angular-Projekt

---

**Entwickler:** Hinni Siemsen
