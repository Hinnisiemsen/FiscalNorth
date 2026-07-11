---
marp: true
theme: default
paginate: true
header: 'Fiscal North — Investor Pitch'
footer: 'Confidential · July 2026'
style: |
  section { font-family: 'IBM Plex Sans', system-ui, sans-serif; }
  h1 { color: #1a365d; }
  h2 { color: #2c5282; }
  strong { color: #2b6cb0; }
---

# Fiscal North
## Investor Pitch Deck

**The AI-native household finance platform for Europe**

*Seed Round · July 2026*

---

# The Problem

**Money is fragmented. Households are invisible to finance apps.**

- Average DACH consumer: **4–8 accounts** across banks, depot, PayPal, crypto
- **23%** of couples have **zero joint accounts** — yet no app supports "yours, mine, ours"
- Subscriptions, contracts, and budgets live in **spreadsheets and separate apps**
- Existing tools: solo-only (Finanzguru) or US-only (Monarch, Honeydue)

> Financial stress is the #2 cause of relationship conflict. The tools weren't built for how people actually live.

---

# The Solution

## Fiscal North — one portfolio, optional sharing, AI guidance

| For individuals | For households (roadmap) |
|-----------------|-------------------------|
| All accounts in one dashboard | Partner invites & shared view |
| AI assistant that **acts** (creates budgets, goals, categories) | Granular privacy per account |
| PSD2 bank sync across EU banks | Shared budgets, goals, splits |
| Contract & subscription tracking | AI "money date" summaries |

**Tagline:** *Personal portfolio. Shared household. AI-guided.*

---

# Product Demo — What's Live Today

✅ **Portfolio:** Checking, savings, depot, crypto, pension, PayPal, cash
✅ **Planning:** Budgets, financial goals, recurring contracts
✅ **Insights:** Dashboard KPIs, category trends, proactive conclusions
✅ **Bank sync:** PSD2 via Berlin Group XS2A (finAPI) — Premium
✅ **AI assistant:** Gemini-powered chat with confirmable actions — Premium
✅ **Freemium:** Free manual tracking → Stripe Premium (14-day trial)
✅ **Multilingual:** DE, EN, ES, FR · Self-hostable Docker stack

---

# Why Now

| Signal | Implication |
|--------|-------------|
| EU PFM market **$3.1B → $10.7B** by 2033 | Large, growing TAM |
| AI-in-finance **$1.1B → $2.95B** by 2030 | Our core differentiator |
| Honeydue stagnant; Zeta acquired | Couples category wide open |
| PSD2 mature; finAPI/Tink commoditize aggregation | Lower build cost |
| Starling, Google launching agentic finance AI | Window to own standalone PFM AI |
| Cost-of-living crisis | Budgeting urgency ↑ |

---

# Market Size

```
TAM  — European PFM apps                    ~$3.1B (2025)
SAM  — DACH + EN/ES/FR multilingual PFM     ~$800M
SOM  — DACH couples + AI-curious solo       ~$120M (Y5 target slice)
```

**Beachhead:** Germany — 83M population, highest fintech adoption in EU, PSD2-native

**Expansion:** AT, CH, FR, ES, NL (languages & PSD2 already supported)

---

# Target Customer

### Primary: DACH couples & cohabiting partners (25–45)

- Dual income, 4–8 accounts combined
- Hybrid finances — some shared, some private
- Frustrated with Finanzguru (solo) or Honeydue (US, broken sync)
- **WTP: €6–8/mo** for reliable sync + AI + partner sharing

### Secondary: Solo professionals (25–35)

- AI-curious, subscription-heavy, goal-oriented
- Free tier → Premium via AI "wow moment"

---

# Business Model

| Tier | Price | What's Included |
|------|-------|-----------------|
| **Free** | €0 | Manual tracking, CSV, dashboard, budgets, goals |
| **Premium** | €4.99/mo · €49/yr | AI assistant, PSD2 sync, AI notifications, goal planner |
| **Household** | €7.99/mo · €79/yr | Premium + 2 seats, shared dashboard, privacy controls |
| **Household Plus** | €11.99/mo · €119/yr | + splits, AI household insights, 4 seats |

**Revenue streams:** Subscriptions (primary) · Future: B2B2C (employer wellness, financial coaches) · Future: affiliate (insurance comparison, like Finanzguru)

**Gross margin target:** >80%

---

# Traction & Milestones

| Milestone | Status |
|-----------|--------|
| Full-stack MVP (backend + frontend) | ✅ Shipped |
| PSD2 bank sync integration | ✅ Integrated |
| AI assistant with actionable proposals | ✅ Live |
| Stripe freemium billing | ✅ Live |
| Multilingual (4 languages) | ✅ Live |
| Self-hostable deployment | ✅ Live |
| CI/CD + Docker production deploy | ✅ Live |
| Shared household portfolio | 🔜 Q4 2026 |
| Mobile app (PWA → native) | 🔜 Q3–Q2 2027 |

*Pre-revenue · Product-complete for solo use case · Ready for GTM*

---

# Competitive Landscape

| | Finanzguru | Outbank | Honeydue | **Fiscal North** |
|--|-----------|---------|----------|------------------|
| Price | Free/€3 | €4/mo | Free | Freemium |
| DACH PSD2 | ✅ | ✅ | ❌ | ✅ |
| AI assistant | ❌ | ❌ | ❌ | ✅ |
| Shared household | ❌ | ❌ | ✅ (US) | 🔜 |
| Self-host | ❌ | Local device | ❌ | ✅ |
| Depot/crypto | Partial | ✅ | Balance only | Account types ✅ |

**We win on:** AI depth + household sharing + EU-native + optional self-host

---

# Go-to-Market

**Phase 1 (0–6 mo):** Solo conversion
- SEO content, Reddit/Finanzen community, influencer partnerships
- Mobile PWA launch
- Target: 5K free users, 250 paid

**Phase 2 (6–12 mo):** Household launch
- Partner invite flow, shared dashboard
- PR: "Honeydue for Europe, but it actually works"
- Target: 15% of paid users on Household tier

**Phase 3 (12–18 mo):** Scale & expand
- Native apps, AT/CH/FR, B2B2C pilots
- Target: 8K paid, €624K ARR

---

# Financial Projections

| | Y1 | Y2 | Y3 | Y4 | Y5 |
|--|----|----|----|----|-----|
| Free users | 5K | 25K | 80K | 200K | 450K |
| Paying users | 250 | 2K | 8K | 22K | 55K |
| ARR | €16K | €144K | €624K | €1.85M | €4.95M |
| Conversion | 5% | 8% | 10% | 11% | 12% |

**Key assumptions:** €6.50 blended ARPU by Y3 · 4% monthly churn · 35% Household attach

---

# The Team

**Hinni Siemsen** — Founder & Developer
- Full-stack builder: Spring Boot, Angular, PSD2, AI integration
- Built Fiscal North end-to-end: backend, frontend, infra, CI/CD
- [LinkedIn / GitHub — insert links]

**Hiring plan (post-seed):**
- Mobile engineer (iOS/Android)
- Growth marketer (DACH fintech)
- Part-time compliance/legal (PSD2/AISP)

---

# The Ask

## Raising **€500K–750K** Seed

| Use of Funds | Allocation |
|--------------|------------|
| Engineering (mobile + household features) | 45% |
| Go-to-market & growth | 30% |
| Infrastructure & PSD2/API costs | 10% |
| Legal, compliance, ops | 10% |
| Buffer | 5% |

**18-month milestones with this raise:**
- 8,000 paying users · €624K ARR run-rate
- Household product live with proven retention uplift
- Native mobile apps in App Store & Play Store
- Ready for Series A

---

# Vision

### Today
Personal finance app with AI assistant for Europe

### 2027
**The household finance OS** — where couples and families see, plan, and optimize money together

### 2030
Embedded AI financial copilot powering **10M+ European households**, with optional white-label for banks and employers

---

# Why Fiscal North Wins

1. **Right wedge** — shared household + AI in underserved DACH market
2. **Product built** — not a slide deck; working full-stack app with PSD2 + AI + billing
3. **Timing** — couples app consolidation + agentic AI wave + PSD2 maturity
4. **Defensibility** — household data graph, AI context, switching costs grow over time
5. **Capital efficient** — solo founder MVP; seed goes to GTM + mobile, not R&D from zero

---

# Thank You

**Let's build the finance app Europe's households deserve.**

Hinni Siemsen
GitHub: github.com/Hinnisiemsen/FiscalNorth

*[Contact email · Calendar link]*
