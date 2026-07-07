# Stripe Billing Setup

FiscalNorth uses [Stripe Checkout](https://stripe.com/docs/payments/checkout) for subscriptions and the [Customer Portal](https://stripe.com/docs/customer-management/customer-portal) for self-service billing management. Subscription state is synchronized via signed webhooks.

## Prerequisites

- Stripe account (test mode for development)
- Stripe CLI for local webhook forwarding (optional)

## 1. Create product and prices

In the [Stripe Dashboard](https://dashboard.stripe.com/products):

1. Create a product **FiscalNorth Premium**
2. Add a **monthly** recurring price
3. Add a **yearly** recurring price (optional but recommended)
4. Copy both **Price IDs** (e.g. `price_...`)

## 2. Configure environment variables

Copy [`.env.example`](../.env.example) to `.env` and set:

| Variable | Description |
|----------|-------------|
| `STRIPE_ENABLED` | `true` to enable billing endpoints |
| `STRIPE_SECRET_KEY` | Secret API key (`sk_test_...` or `sk_live_...`) |
| `STRIPE_WEBHOOK_SECRET` | Signing secret from webhook endpoint (`whsec_...`) |
| `STRIPE_PRICE_ID_MONTHLY` | Monthly price ID |
| `STRIPE_PRICE_ID_YEARLY` | Yearly price ID |
| `STRIPE_TRIAL_DAYS` | Free trial length (default: 14) |
| `STRIPE_PAST_DUE_GRACE_DAYS` | Grace period after failed payment (default: 3) |

Frontend redirect URLs are derived from `APP_AUTH_FRONTEND_URL` / `app.auth.frontend-url`.

## 3. Configure webhooks

### Production

Add endpoint:

```
POST https://<your-host>/api/billing/webhook
```

Subscribe to events:

- `checkout.session.completed`
- `customer.subscription.created`
- `customer.subscription.updated`
- `customer.subscription.deleted`
- `invoice.payment_failed`

Copy the **Signing secret** to `STRIPE_WEBHOOK_SECRET`.

### Local development

```bash
stripe listen --forward-to localhost:8080/api/billing/webhook
```

Use the CLI signing secret as `STRIPE_WEBHOOK_SECRET`.

## 4. Enable Customer Portal

In Stripe Dashboard → **Settings → Billing → Customer portal**:

- Enable subscription cancellation
- Enable payment method updates
- Enable invoice history

## Premium features

| Feature | Entitlement |
|---------|-------------|
| AI Assistant | `AI_ASSISTANT` |
| AI Goal Planner | `AI_GOAL_PLANNER` |
| Bank Sync (PSD2) | `BANK_SYNC` |
| AI cron notifications | `AI_NOTIFICATIONS` |

Admin users receive all entitlements without a Stripe subscription.

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/billing/subscription` | Current plan and entitlements |
| GET | `/api/billing/plans` | Available Stripe prices |
| POST | `/api/billing/checkout-session` | Start Stripe Checkout |
| POST | `/api/billing/portal-session` | Open Customer Portal |
| POST | `/api/billing/webhook` | Stripe webhook receiver |

## Architecture notes

- Stripe is the billing source of truth; the `user_subscription` table is updated from webhooks.
- Webhook processing is idempotent (`processed_stripe_event` stores event IDs).
- Entitlements are enforced server-side in services; the frontend shows paywalls for discovery only.
- When `STRIPE_ENABLED=false`, all users remain on the free tier and billing endpoints report billing as unavailable.
