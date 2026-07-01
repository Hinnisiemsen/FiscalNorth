# Deploying Fiscal North

Fiscal North ships as Docker containers. For production, run the stack on a VPS with a reverse proxy for HTTPS. CI builds and pushes images to GitHub Container Registry (ghcr.io); merging to `master` can automatically redeploy the server.

## Architecture

```
Internet
   │
   ▼
Caddy / Traefik / nginx  (TLS on :443)
   │
   ▼
frontend container       (127.0.0.1:3000 → nginx → /api, /oauth2, /login proxy)
   │
   ▼
backend container        (Spring Boot, internal only)
   ├── postgres
   └── rabbitmq
```

The Angular app and API share one public origin. The frontend nginx container proxies `/api/`, `/oauth2/`, and `/login/` to the backend, which keeps session cookies and CSRF working without CORS issues.

## What CI already does

| Workflow | On merge to `master` |
|----------|----------------------|
| **CI** | Runs unit/integration tests, frontend tests, and a Docker Compose smoke test |
| **Docker Build** | Builds and pushes `ghcr.io/<owner>/fiscalnorth-backend:latest` and `ghcr.io/<owner>/fiscalnorth-frontend:latest` |
| **Deploy** | SSHs into your VPS and runs `./scripts/deploy.sh` (when enabled) |

---

## One-time server setup

### 1. Create a VPS

- **2 GB RAM** or more recommended
- Ubuntu 22.04/24.04 LTS works well
- Open firewall ports **22**, **80**, and **443** only

### 2. Install Docker

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker "$USER"
newgrp docker
```

Install the Compose plugin if it is not bundled:

```bash
sudo apt-get update && sudo apt-get install -y docker-compose-plugin
```

### 3. Clone the repository

```bash
sudo mkdir -p /opt/fiscalnorth
sudo chown "$USER":"$USER" /opt/fiscalnorth
git clone https://github.com/Hinnisiemsen/FiscalNorth.git /opt/fiscalnorth
cd /opt/fiscalnorth
```

### 4. Configure production environment

```bash
cp .env.production.example .env.production
$EDITOR .env.production
```

Required values:

| Variable | Example | Notes |
|----------|---------|-------|
| `APP_AUTH_FRONTEND_URL` | `https://app.example.com` | Must match your public URL exactly |
| `POSTGRES_PASSWORD` | long random string | Do not reuse dev defaults |
| `RABBITMQ_PASSWORD` | long random string | Do not reuse dev defaults |
| `GOOGLE_CLIENT_ID` | from Google Cloud | Real OAuth client, not the local placeholder |
| `GOOGLE_CLIENT_SECRET` | from Google Cloud | Pair with client ID |
| `GEMINI_API_KEY` | optional | Needed for AI assistant features |

### 5. Allow the server to pull images from ghcr.io

Create a GitHub personal access token (classic) with **`read:packages`**.

On the server:

```bash
echo "<YOUR_GITHUB_PAT>" | docker login ghcr.io -u <github-username> --password-stdin
```

Docker stores credentials in `~/.docker/config.json`. Repeat after token rotation.

If package visibility is public, login may not be required.

### 6. Point DNS at the server

```
app.example.com  A  <server-public-ip>
```

### 7. Configure Google OAuth

In [Google Cloud Console](https://console.cloud.google.com/), add this authorized redirect URI:

```
https://app.example.com/login/oauth2/code/google
```

Use the same domain as `APP_AUTH_FRONTEND_URL`.

### 8. Add HTTPS with Caddy

Install Caddy, then create `/etc/caddy/Caddyfile`:

```caddy
app.example.com {
    reverse_proxy 127.0.0.1:3000
}
```

Reload Caddy:

```bash
sudo systemctl reload caddy
```

The production compose file binds the frontend to **127.0.0.1:3000** so only the reverse proxy is public.

### 9. First deploy

```bash
cd /opt/fiscalnorth
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

Verify:

```bash
curl -s https://app.example.com/api/auth/status
curl -sI https://app.example.com/ | head
```

---

## Automatic deploy on merge to `master`

The **Deploy** workflow runs after **Docker Build** succeeds on `master`. It connects over SSH and executes `./scripts/deploy.sh` on your server.

### Enable auto-deploy in GitHub

1. Open **Settings → Secrets and variables → Actions**.
2. Add **repository secrets**:

| Secret | Description |
|--------|-------------|
| `DEPLOY_HOST` | Public IP or hostname of the VPS |
| `DEPLOY_USER` | SSH user (e.g. `ubuntu`) |
| `DEPLOY_SSH_KEY` | Private key for that user (PEM, including `-----BEGIN...`) |
| `DEPLOY_PATH` | Optional. Default `/opt/fiscalnorth` |
| `DEPLOY_PORT` | Optional. Default `22` |
| `GHCR_READ_TOKEN` | Optional if the server already ran `docker login ghcr.io` |

3. Add a **repository variable**:

| Variable | Value |
|----------|-------|
| `DEPLOY_ENABLED` | `true` |

Until `DEPLOY_ENABLED=true`, the Deploy workflow skips cleanly so merges do not fail while you are still setting up the server.

4. (Recommended) Create a **production** environment under **Settings → Environments** and attach the deploy secrets there for clearer audit history.

### Generate a deploy SSH key

On your laptop:

```bash
ssh-keygen -t ed25519 -f fiscalnorth-deploy -N ""
```

- Put **`fiscalnorth-deploy.pub`** into `~/.ssh/authorized_keys` on the VPS.
- Put the contents of **`fiscalnorth-deploy`** into the `DEPLOY_SSH_KEY` secret.

Restrict the key to deployment if you prefer:

```bash
command="/opt/fiscalnorth/scripts/deploy.sh",no-port-forwarding,no-X11-forwarding,no-agent-forwarding ssh-ed25519 AAAA...
```

### What happens on each merge to `master`

1. CI and Docker Build run on the new commit.
2. When Docker Build succeeds, **Deploy** starts.
3. The workflow SSHs to the VPS, runs `git fetch && git reset --hard origin/master`, optionally logs in to ghcr.io, then runs `./scripts/deploy.sh`.
4. `deploy.sh` pulls the latest images and recreates containers with `compose.prod.yaml`.

### Manual redeploy

**Actions → Deploy → Run workflow** triggers the same SSH deploy without a code change.

---

## Local vs production compose files

| File | Purpose |
|------|---------|
| `compose.yaml` | Local development: builds from source, seeds demo data, exposes ports for debugging |
| `compose.prod.yaml` | Production: pulls ghcr.io images, no demo seed, DB/message broker not exposed publicly |

Local:

```bash
cp .env.example .env
docker compose up -d --build
```

Production:

```bash
cp .env.production.example .env.production
./scripts/deploy.sh
```

---

## Production checklist

- [ ] Strong passwords for Postgres and RabbitMQ
- [ ] `SPRING_SQL_INIT_MODE=never` (already set in `compose.prod.yaml`)
- [ ] Real Google OAuth credentials and redirect URI for your domain
- [ ] `APP_AUTH_FRONTEND_URL` matches the HTTPS URL
- [ ] TLS enabled (Caddy/Let's Encrypt or equivalent)
- [ ] Postgres volume backups scheduled
- [ ] ghcr.io login configured on the server
- [ ] GitHub deploy secrets and `DEPLOY_ENABLED=true` configured

---

## Troubleshooting

### Backend fails with “Client id of registration 'google' must not be empty”

Set real `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` in `.env.production`. Empty values are rejected by Spring Boot.

### OAuth redirect mismatch

Ensure Google Cloud redirect URI is `https://<domain>/login/oauth2/code/google` and matches the URL users actually open.

### 502 from reverse proxy

Check containers:

```bash
docker compose -f compose.prod.yaml --env-file .env.production ps
docker compose -f compose.prod.yaml --env-file .env.production logs backend --tail 100
```

### Deploy workflow skipped

Confirm repository variable `DEPLOY_ENABLED` is exactly `true`.

### Deploy workflow fails on SSH

Verify `DEPLOY_HOST`, `DEPLOY_USER`, `DEPLOY_SSH_KEY`, and that the public key is in `authorized_keys` on the server.

---

## Related docs

- [AUTH.md](AUTH.md) – authentication, OAuth, sessions
- [README.md](../README.md) – local development and CI overview
