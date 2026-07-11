# 🏠 FiscalNorth

Ein modernes Finanz-Management-System bestehend aus einem Spring-Boot-Backend und einer Angular-Frontend-Anwendung. Die App ermöglicht das Management von Bankkonten, Budgets, Verträgen und Transaktionen – inklusive Bank-Sync über Berlin Group XS2A (PSD2) und KI-gestützte Analysefunktionen.

## 📦 Projektstruktur

```
fiscalNorth/
├── backend/          # Spring Boot API (Java 21)
├── frontend/         # Angular 20 SPA
├── xs2a-client/      # OpenAPI-generierter Berlin Group XS2A Client (finAPI)
└── compose.yaml      # Docker Compose (PostgreSQL, RabbitMQ, Backend, Frontend)
```

## 🚀 Features

### Kernfunktionen
* **Kontenverwaltung** – Girokonto, Sparkonto, Festgeld (DepositAccount), Krypto, Bargeld, Depot, PayPal
* **Transaktionsmanagement** – Ausgaben, Einnahmen, Umbuchungen, Kategorisierung, Split-Buchungen
* **Vertragsmanagement** – Wiederkehrende Zahlungen mit verschiedenen Intervallen
* **Budgetierung** – Ausgabenlimits pro Zeitraum inkl. Nutzungsanzeige
* **Kategorien** – Eigene Kategorien für Transaktionen

### Erweiterte Funktionen
* **Bank-Sync (XS2A)** – PSD2-konforme Anbindung an Banken über finAPI Sandbox
* **CSV-Import** – Transaktionen aus CSV-Dateien importieren
* **Insights** – Monatliche Trends und Ausgaben nach Kategorien
* **KI-Integration** – Spring AI (Mistral) für Vertragsanalyse und Dokumentenverarbeitung

## 🛠 Tech Stack

| Komponente | Technologien |
|------------|--------------|
| **Backend** | Java 21, Spring Boot 3.3.5, Spring Data JPA, Spring Security |
| **API** | Spring WebFlux (Reactive), Spring Data REST |
| **Datenbank** | PostgreSQL (Produktion), H2 (lokale Entwicklung) |
| **Messaging** | RabbitMQ, Apache Kafka |
| **AI** | Spring AI (Mistral AI), PDF-Dokumentenanalyse |
| **Frontend** | Angular 20, TypeScript |
| **XS2A** | Berlin Group NextGenPSD2 Framework (finAPI Client) |

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

**1. Infrastruktur starten (PostgreSQL, RabbitMQ):**
```bash
docker compose up -d postgres rabbitmq
```

**2. Backend starten:**
```bash
# Im Projektroot (baut xs2a-client + backend)
./backend/mvnw -f pom.xml -pl backend spring-boot:run
```

**3. Frontend starten:**
```bash
cd frontend
npm install
ng serve
```

Frontend: http://localhost:4200

**Hinweis:** In der lokalen Entwicklung verwendet das Backend standardmäßig H2 (Speicher-DB). Für PostgreSQL `spring.docker.compose.enabled=true` setzen oder die Datenquelle manuell konfigurieren.

## 📚 API Übersicht

| Bereich | Endpunkte |
|---------|-----------|
| **Bankkonten** | `GET/POST /api/account/bank`, `GET /api/account/bank/{id}` |
| **Festgeld** | `GET/POST /api/account/deposit`, `GET/DELETE /api/account/deposit/{id}` |
| **Transaktionen** | `GET/POST /api/transaction/payment`, `GET /api/transaction/transfer` |
| **CSV-Import** | `POST /api/transaction/import/csv` |
| **Insights** | `GET /api/transaction/insights` |
| **Verträge** | `GET/POST /api/contract`, `POST /api/contract/analyze` |
| **Budgets** | `GET/POST /api/budget`, `GET /api/budget/with-usage` |
| **Kategorien** | `GET/POST/DELETE /api/category` |
| **Bank-Sync** | `GET /api/bank-sync/status`, `POST /api/bank-sync/consent`, `POST /api/bank-sync/sync` |
| **User** | `GET/POST /api/user` |

## 🧪 Testen

```bash
./backend/mvnw -f pom.xml test
```

Integrationstests nutzen Testcontainers (Kafka, RabbitMQ, PostgreSQL).

## 🔄 CI/CD (GitHub Actions)

| Workflow | Trigger | Beschreibung |
|----------|---------|--------------|
| **CI** | Push/PR auf main/master | Backend: Maven build + Tests. Frontend: npm build + Karma Tests. Docker Compose smoke test |
| **Deploy** | Nach Docker Build auf master (wenn `DEPLOY_ENABLED=true`) | SSH-Deploy auf VPS via `compose.prod.yaml` |
| **Docker Build** | Push/PR, Release | Baut Backend- und Frontend-Images, push zu ghcr.io bei Push/Release |
| **Lint** | PR (nur bei Änderungen in frontend/) | Prettier-Check für TypeScript, HTML, CSS |

## 📝 Konfiguration

| Datei | Beschreibung |
|-------|--------------|
| `backend/src/main/resources/application.properties` | Hauptkonfiguration |
| `compose.yaml` | Docker-Services und Umgebungsvariablen |

### Wichtige Einstellungen
* `spring.jackson.mapper.accept-case-insensitive-enums=true` – Flexiblere Enum-Deserialisierung
* `app.xs2a.enabled` – Bank-Sync aktivieren/deaktivieren
* `app.xs2a.base-url` – finAPI XS2A Endpoint (z.B. Sandbox)
* `GEMINI_API_KEY` – Google Gemini API-Key (Compose: via `.env` oder Host-Umgebung)
* `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` – OAuth für Docker-Stack (Redirect: `http://localhost:8080/login/oauth2/code/google`)

## 📖 Weitere Dokumentation

* [ROADMAP.md](ROADMAP.md) – Product roadmap and planned features
* [docs/AUTH.md](docs/AUTH.md) – Authentication, OAuth, user isolation, demo login
* [docs/DEPLOY.md](docs/DEPLOY.md) – Production deployment and auto-deploy on merge to master
* [xs2a-client/README.md](xs2a-client/README.md) – Berlin Group XS2A API Client
* [frontend/README.md](frontend/README.md) – Angular-Projekt

---

**Entwickler:** Hinni Siemsen
