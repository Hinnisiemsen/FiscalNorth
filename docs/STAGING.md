# Staging / premium feature activation

Use this checklist when enabling premium features in a staging environment (without relying on demo preview mode).

## Demo preview mode (local / portfolio)

By default, local development grants all premium features without Stripe:

| Variable | Default | Description |
|----------|---------|-------------|
| `DEMO_PREMIUM_PREVIEW` | `true` | Grants AI assistant, goal planner, bank sync, and AI notifications |

Set `DEMO_PREMIUM_PREVIEW=false` in production. The paywall banner remains visible until dismissed or until the user has a paid Stripe subscription.

## Stripe (staging)

See [BILLING.md](BILLING.md). For staging:

```bash
STRIPE_ENABLED=true
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_PRICE_ID_MONTHLY=price_...
STRIPE_PRICE_ID_YEARLY=price_...
DEMO_PREMIUM_PREVIEW=false
```

Forward webhooks locally:

```bash
stripe listen --forward-to localhost:8080/api/billing/webhook
```

## Bank sync / XS2A (staging)

```bash
XS2A_ENABLED=true
app.xs2a.access-token=your-finapi-bearer-token
```

Configure redirect URI in finAPI to match `app.xs2a.redirect-uri` (default: `http://localhost:4200/bank-sync/callback`).

## Docker Compose

Add to `.env`:

```bash
DEMO_PREMIUM_PREVIEW=true
STRIPE_ENABLED=false
XS2A_ENABLED=false
GEMINI_API_KEY=your-key
```

For staging with live Stripe and bank sync, set `STRIPE_ENABLED=true`, `XS2A_ENABLED=true`, and `DEMO_PREMIUM_PREVIEW=false`.

## Verify premium flows

1. Sign in as demo user (`alex@fiscalnorth.local` / `demo1234`)
2. Open `/assistant` — dismiss paywall with **Try in demo** or use features directly when preview is enabled
3. Open `/account/upgrade` — pricing UI visible; checkout works when Stripe is configured
4. Open `/bank-sync` — connect flow when XS2A is enabled and entitled
