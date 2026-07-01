#!/usr/bin/env bash
# Bootstrap FiscalNorth on a Google Compute Engine VM (Ubuntu 24.04).
# Run as root or with sudo on a fresh VM, then finish .env.production and deploy.
#
# Example (from Cloud Shell or laptop with gcloud):
#   gcloud compute scp scripts/gcp-bootstrap.sh fiscalnorth-vm:/tmp/ --zone=europe-west3-a
#   gcloud compute ssh fiscalnorth-vm --zone=europe-west3-a --command="sudo bash /tmp/gcp-bootstrap.sh"

set -euo pipefail

DEPLOY_USER="${DEPLOY_USER:-fiscalnorth}"
DEPLOY_PATH="${DEPLOY_PATH:-/opt/fiscalnorth}"
REPO_URL="${REPO_URL:-https://github.com/Hinnisiemsen/FiscalNorth.git}"
DOMAIN="${DOMAIN:-}"  # optional: set for Caddy, e.g. app.example.com

echo "==> Installing Docker..."
curl -fsSL https://get.docker.com | sh
apt-get update
apt-get install -y docker-compose-plugin git

if ! id "$DEPLOY_USER" &>/dev/null; then
  echo "==> Creating user ${DEPLOY_USER}..."
  useradd -m -s /bin/bash "$DEPLOY_USER"
fi
usermod -aG docker "$DEPLOY_USER"

echo "==> Cloning repository to ${DEPLOY_PATH}..."
mkdir -p "$DEPLOY_PATH"
chown "$DEPLOY_USER":"$DEPLOY_USER" "$DEPLOY_PATH"
if [ ! -d "${DEPLOY_PATH}/.git" ]; then
  sudo -u "$DEPLOY_USER" git clone "$REPO_URL" "$DEPLOY_PATH"
fi

sudo -u "$DEPLOY_USER" bash -c "
  cd '${DEPLOY_PATH}'
  if [ ! -f .env.production ]; then
    cp .env.production.example .env.production
    echo 'Created .env.production — edit before first deploy.'
  fi
  chmod +x scripts/deploy.sh
"

if [ -n "$DOMAIN" ]; then
  echo "==> Installing Caddy for ${DOMAIN}..."
  apt-get install -y debian-keyring debian-archive-keyring apt-transport-https curl
  curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
  curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | tee /etc/apt/sources.list.d/caddy-stable.list
  apt-get update
  apt-get install -y caddy
  cat > /etc/caddy/Caddyfile <<EOF
${DOMAIN} {
    reverse_proxy 127.0.0.1:3000
}
EOF
  systemctl enable caddy
  systemctl reload caddy
fi

cat <<EOF

Bootstrap complete.

Next steps (as ${DEPLOY_USER}):
  1. Edit ${DEPLOY_PATH}/.env.production
     - APP_AUTH_FRONTEND_URL=https://<your-domain>
     - POSTGRES_PASSWORD, RABBITMQ_PASSWORD
     - GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET (from same GCP project)
     - GEMINI_API_KEY (optional)
  2. docker login ghcr.io -u <github-user>   # GitHub PAT with read:packages
  3. cd ${DEPLOY_PATH} && ./scripts/deploy.sh

Configure Google OAuth redirect URI:
  https://<your-domain>/login/oauth2/code/google

EOF
