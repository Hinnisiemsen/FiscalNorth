# Google Cloud AI Assist prompt — FiscalNorth production deploy

Copy everything below the line into **Google Cloud AI Assist** (or Gemini in Cloud Console). Replace `[PLACEHOLDERS]` first.

---

```
I want to deploy "FiscalNorth" (Spring Boot + Angular finance app) to Google Cloud production using a Compute Engine VM and Docker Compose. Guide me through the Google Cloud Console and gcloud commands.

## My details

- **GCP project ID:** [PROJECT_ID]
- **Region:** [e.g. europe-west3]
- **Zone:** [e.g. europe-west3-a]
- **VM name:** fiscalnorth-vm
- **Public domain:** [app.example.com]
- **GitHub repo:** https://github.com/Hinnisiemsen/FiscalNorth
- **GitHub owner (ghcr.io):** hinnisiemsen
- **Deploy path on VM:** /opt/fiscalnorth
- **Linux user on VM:** fiscalnorth

## Target architecture

- One GCE VM (e2-small, Ubuntu 24.04, 30 GB disk) with a **reserved static external IP**
- VPC firewall: allow TCP 22, 80, 443 to instances tagged `fiscalnorth` only
- **Cloud DNS** A record: [app.example.com] → static IP (or tell me how to do this at my external registrar)
- **Caddy** on the VM for Let's Encrypt TLS, reverse_proxy to 127.0.0.1:3000
- **Docker Compose** (`compose.prod.yaml`) pulls pre-built images:
  - ghcr.io/hinnisiemsen/fiscalnorth-backend:latest
  - ghcr.io/hinnisiemsen/fiscalnorth-frontend:latest
- Containers on the VM: postgres, rabbitmq, backend, frontend (frontend bound to 127.0.0.1:3000)
- Do NOT expose postgres (5432), rabbitmq (5672), or backend (8080) to the internet

## Google Cloud services I need configured in THIS project

### 1. OAuth 2.0 (Sign in with Google)

Spring Security OAuth2 redirect URI (exact):

  https://[app.example.com]/login/oauth2/code/google

Authorized JavaScript origin:

  https://[app.example.com]

Walk me through OAuth consent screen + Web application client ID. I need the Client ID and Client Secret for `.env.production`.

### 2. Gemini API (optional AI assistant)

Enable Generative Language API, create an API key, suggest restrictions. Model used: gemini-2.5-flash. Env var: GEMINI_API_KEY.

## What to generate for me

### A. Full gcloud command sequence

Include, in order:
1. Set project, enable APIs (compute, dns if Cloud DNS)
2. Create static regional IP `fiscalnorth-ip`
3. Create firewall rules `fiscalnorth-allow-web` and `fiscalnorth-allow-ssh`
4. Create VM with tag `fiscalnorth`, static IP, e2-small, ubuntu-2404-lts
5. Cloud DNS zone + A record (if using Cloud DNS)
6. Commands to copy and run bootstrap script from the repo: `scripts/gcp-bootstrap.sh` with `DOMAIN=[app.example.com]`

### B. Bootstrap script actions (already in repo as scripts/gcp-bootstrap.sh)

Confirm it should: install Docker, create user `fiscalnorth`, clone repo to /opt/fiscalnorth, copy .env.production.example, install Caddy for my domain.

### C. Complete `.env.production` template

APP_AUTH_FRONTEND_URL=https://[app.example.com]
FRONTEND_PORT=3000
IMAGE_TAG=latest
GITHUB_REPOSITORY_OWNER=hinnisiemsen
POSTGRES_DB=fiscalnorth
POSTGRES_USER=fiscalnorth
POSTGRES_PASSWORD=[GENERATE_SECURE]
RABBITMQ_USER=fiscalnorth
RABBITMQ_PASSWORD=[GENERATE_SECURE]
GOOGLE_CLIENT_ID=[FROM_OAUTH]
GOOGLE_CLIENT_SECRET=[FROM_OAUTH]
GEMINI_API_KEY=[OPTIONAL]
AI_ENABLED=true

### D. Post-deploy verification

- curl https://[app.example.com]/api/auth/status
- curl -I https://[app.example.com]/
- docker compose -f compose.prod.yaml ps

### E. GitHub Actions auto-deploy (SSH to GCE VM)

After VM works, I will set GitHub secrets:
- DEPLOY_HOST = static IP or domain
- DEPLOY_USER = fiscalnorth
- DEPLOY_SSH_KEY = ed25519 private key
- DEPLOY_ENABLED = true (repository variable)

Give me commands to generate the deploy key and add it to the VM's authorized_keys via gcloud compute ssh.

## Constraints

- APP_AUTH_FRONTEND_URL must exactly match the HTTPS URL users open
- Production must not load demo seed data (SPRING_SQL_INIT_MODE=never in compose.prod.yaml)
- Real Google OAuth credentials required (empty client ID crashes Spring Boot)
- ghcr.io pull requires: docker login ghcr.io with GitHub PAT (read:packages) OR public packages

## Output format

1. Numbered Console steps where clicks matter
2. Copy-paste gcloud blocks
3. .env.production filled template with [REPLACE] markers
4. Verification checklist
5. Troubleshooting: OAuth mismatch, ghcr pull auth, Caddy 502, firewall

Ask me only for values you cannot infer from the placeholders above.
```
