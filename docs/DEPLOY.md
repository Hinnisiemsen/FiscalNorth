# Deploying Fiscal North on Google Cloud

Fiscal North runs in Docker on a **Google Compute Engine (GCE) VM**. CI builds images to GitHub Container Registry (ghcr.io); merging to `master` can auto-deploy over SSH to that VM.

## Architecture

```
Internet
   │
   ▼
Cloud DNS  →  Static external IP  →  GCE VM
                                        │
                                        ▼
                              Caddy (TLS :443)
                                        │
                                        ▼
                              frontend :3000 (nginx proxy)
                                        │
                                        ▼
                              backend (Spring Boot)
                                 ├── Cloud SQL optional later
                                 ├── postgres (container, default)
                                 └── rabbitmq (container)
```

OAuth and Gemini API keys come from the **same Google Cloud project** (APIs & Services + OAuth credentials).

The frontend nginx container proxies `/api/`, `/oauth2/`, and `/login/` to the backend so session cookies and CSRF work on one public origin.

## What CI does on merge to `master`

| Workflow | Action |
|----------|--------|
| **CI** | Tests + Docker Compose smoke test |
| **Docker Build** | Pushes `ghcr.io/hinnisiemsen/fiscalnorth-backend:latest` and `...-frontend:latest` |
| **Deploy** | SSH to GCE VM → `./scripts/deploy.sh` (when `DEPLOY_ENABLED=true`) |

---

## One-time Google Cloud setup

Replace placeholders: `PROJECT_ID`, `REGION`, `ZONE`, `VM_NAME`, `DOMAIN`.

### 1. Create / select a project

```bash
gcloud config set project PROJECT_ID
gcloud services enable compute.googleapis.com dns.googleapis.com
```

### 2. Reserve a static external IP

```bash
gcloud compute addresses create fiscalnorth-ip --region=REGION
gcloud compute addresses describe fiscalnorth-ip --region=REGION --format='get(address)'
```

Note the IP for DNS.

### 3. Create firewall rules

Allow SSH, HTTP, and HTTPS only:

```bash
gcloud compute firewall-rules create fiscalnorth-allow-web \
  --allow=tcp:80,tcp:443 \
  --target-tags=fiscalnorth \
  --description="HTTP/HTTPS for FiscalNorth"

gcloud compute firewall-rules create fiscalnorth-allow-ssh \
  --allow=tcp:22 \
  --target-tags=fiscalnorth \
  --source-ranges=0.0.0.0/0 \
  --description="SSH (restrict source-ranges to your IP in production)"
```

### 4. Create the VM

```bash
gcloud compute instances create fiscalnorth-vm \
  --zone=ZONE \
  --machine-type=e2-small \
  --image-family=ubuntu-2404-lts-amd64 \
  --image-project=ubuntu-os-cloud \
  --boot-disk-size=30GB \
  --tags=fiscalnorth \
  --address=fiscalnorth-ip
```

`e2-small` (2 GB RAM) is the minimum recommended size.

### 5. Point DNS (Cloud DNS or your registrar)

**Cloud DNS:**

```bash
gcloud dns managed-zones create fiscalnorth-zone --dns-name=example.com. --description="FiscalNorth"
# Add A record app.example.com → static IP in Console or:
gcloud dns record-sets transaction start --zone=fiscalnorth-zone
gcloud dns record-sets transaction add --zone=fiscalnorth-zone --name=app.example.com. --type=A --ttl=300 STATIC_IP
gcloud dns record-sets transaction execute --zone=fiscalnorth-zone
```

**External registrar:** `A  app  →  STATIC_IP`

### 6. Configure Google OAuth (same GCP project)

1. **APIs & Services → OAuth consent screen** — configure (External).
2. **APIs & Services → Credentials → Create OAuth client ID → Web application**
3. **Authorized JavaScript origins:** `https://app.example.com`
4. **Authorized redirect URIs:**

   ```
   https://app.example.com/login/oauth2/code/google
   ```

5. Copy **Client ID** and **Client secret** into `.env.production`.

### 7. Enable Gemini API (optional, for AI assistant)

1. **APIs & Services → Library** → enable **Generative Language API**.
2. **APIs & Services → Credentials → Create API key**.
3. Restrict the key (HTTP referrer = your domain, or IP = VM static IP).
4. Set `GEMINI_API_KEY` in `.env.production`.

### 8. Bootstrap the VM

From Cloud Shell or your laptop:

```bash
gcloud compute scp scripts/gcp-bootstrap.sh fiscalnorth-vm:/tmp/ --zone=ZONE
gcloud compute ssh fiscalnorth-vm --zone=ZONE --command="sudo DOMAIN=app.example.com bash /tmp/gcp-bootstrap.sh"
```

Or SSH in manually and run the script from a cloned repo.

### 9. Configure `.env.production` on the VM

```bash
gcloud compute ssh fiscalnorth-vm --zone=ZONE
sudo -u fiscalnorth nano /opt/fiscalnorth/.env.production
```

Required:

| Variable | Example |
|----------|---------|
| `APP_AUTH_FRONTEND_URL` | `https://app.example.com` |
| `POSTGRES_PASSWORD` | long random string |
| `RABBITMQ_PASSWORD` | long random string |
| `GOOGLE_CLIENT_ID` | from GCP Credentials |
| `GOOGLE_CLIENT_SECRET` | from GCP Credentials |
| `GEMINI_API_KEY` | optional |

### 10. Log in to ghcr.io and deploy

```bash
echo "GITHUB_PAT" | docker login ghcr.io -u Hinnisiemsen --password-stdin
cd /opt/fiscalnorth && ./scripts/deploy.sh
```

Verify:

```bash
curl -s https://app.example.com/api/auth/status
```

---

## Automatic deploy on merge to `master`

### GitHub secrets (Settings → Secrets and variables → Actions)

| Secret | Value |
|--------|--------|
| `DEPLOY_HOST` | GCE static external IP or `app.example.com` |
| `DEPLOY_USER` | `fiscalnorth` (from bootstrap script) |
| `DEPLOY_SSH_KEY` | Private deploy key (PEM) |
| `DEPLOY_PATH` | `/opt/fiscalnorth` (optional) |
| `GHCR_READ_TOKEN` | GitHub PAT with `read:packages` (optional if VM already logged in) |
| `DEPLOY_URL` | `https://app.example.com` (optional post-deploy smoke check) |

### Repository variable

| Variable | Value |
|----------|-------|
| `DEPLOY_ENABLED` | `true` |

### Deploy SSH key on the VM

On your laptop:

```bash
ssh-keygen -t ed25519 -f fiscalnorth-deploy -N ""
```

Add the public key to the VM:

```bash
gcloud compute ssh fiscalnorth-vm --zone=ZONE
echo "<paste fiscalnorth-deploy.pub>" >> /home/fiscalnorth/.ssh/authorized_keys
```

Put the **private** key contents into GitHub secret `DEPLOY_SSH_KEY`.

### Deploy flow

1. Merge to `master` → CI + Docker Build run.
2. **Deploy** workflow SSHs to the GCE VM.
3. `git reset --hard origin/master` → `./scripts/deploy.sh` → pulls latest ghcr.io images.

Manual redeploy: **Actions → Deploy → Run workflow**.

---

## Compose files

| File | Use |
|------|-----|
| `compose.yaml` | Local dev (build from source, demo seed) |
| `compose.prod.yaml` | Production on GCE (ghcr.io images, no public DB ports) |

```bash
# Local
docker compose up -d --build

# Production (on VM)
./scripts/deploy.sh
```

---

## Production checklist

- [ ] Static IP attached to VM
- [ ] Firewall: only 22, 80, 443 (restrict SSH source in production)
- [ ] Cloud DNS or registrar A record → static IP
- [ ] OAuth redirect URI matches `https://<domain>/login/oauth2/code/google`
- [ ] `APP_AUTH_FRONTEND_URL` matches public HTTPS URL
- [ ] Strong Postgres/RabbitMQ passwords in `.env.production`
- [ ] ghcr.io login on VM
- [ ] `DEPLOY_ENABLED=true` + GitHub deploy secrets
- [ ] Optional: schedule Postgres volume backups (snapshot disk or `pg_dump` cron)

---

## Troubleshooting

### OAuth redirect mismatch

Redirect URI in GCP must be `https://<domain>/login/oauth2/code/google` (via frontend proxy, not `:8080`).

### Cannot pull ghcr.io images

Run `docker login ghcr.io` on the VM or set `GHCR_READ_TOKEN` in GitHub secrets.

### 502 from Caddy

```bash
docker compose -f compose.prod.yaml --env-file .env.production ps
docker compose -f compose.prod.yaml --env-file .env.production logs backend --tail 100
```

### Deploy workflow skipped

Set repository variable `DEPLOY_ENABLED=true`.

### SSH deploy fails

Confirm `DEPLOY_HOST` is the VM external IP, `DEPLOY_USER=fiscalnorth`, and the deploy public key is in `authorized_keys`.

---

## Google Cloud AI Assist prompt

Copy the prompt in [DEPLOY-GCP-AI-PROMPT.md](DEPLOY-GCP-AI-PROMPT.md) into **Google Cloud AI Assist** for guided Console + `gcloud` setup.

## Related docs

- [AUTH.md](AUTH.md) – authentication and sessions
- [README.md](../README.md) – local development
