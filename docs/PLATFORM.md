# Platform extensions

FiscalNorth includes hooks for async messaging and real-time updates. RabbitMQ and Kafka run in Docker Compose for local infra; application code uses lightweight abstractions today.

## Event publishing

[`PlatformEventPublisher`](../backend/src/main/java/de/fiscalnorth/platform/PlatformEventPublisher.java) publishes in-process Spring events via [`PlatformEvent`](../backend/src/main/java/de/fiscalnorth/platform/PlatformEvent.java). This is the integration point for a future Kafka or RabbitMQ bridge without changing domain services.

## WebSocket notifications

[`WebSocketConfig`](../backend/src/main/java/de/fiscalnorth/platform/WebSocketConfig.java) enables STOMP over `/ws` with topic prefix `/topic`. Future work: push budget alerts and bank-sync status to `/topic/notifications`.

## Crypto accounts

[`CryptoAccount`](../backend/src/main/java/de/fiscalnorth/account/model/CryptoAccount.java) extends the polymorphic account model (discriminator `CRYPTO`). API: `GET/POST /api/account/crypto`.

## Admin tools

[`AdminController`](../backend/src/main/java/de/fiscalnorth/admin/AdminController.java) exposes `GET /api/admin/users` for users with role `Admin`. The existing `GET /api/user` endpoint also lists users for admins via `@PreAuthorize`.

## Next steps (optional)

| Component | Compose service | Application status |
|-----------|-----------------|-------------------|
| RabbitMQ | Running | No listeners yet — use for async bank sync / AI jobs |
| Kafka | Testcontainers in CI | No producers/consumers — use for audit/analytics pipeline |
| WebSocket | Configured | No notification push yet — subscribe clients to `/topic/notifications` |
