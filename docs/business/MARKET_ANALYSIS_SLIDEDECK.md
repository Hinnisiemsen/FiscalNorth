---
marp: true
theme: default
paginate: true
header: 'Fiscal North — Market Analysis'
footer: 'Confidential · July 2026'
style: |
  section { font-family: 'IBM Plex Sans', system-ui, sans-serif; }
  h1 { color: #1a365d; }
  h2 { color: #2c5282; }
  strong { color: #2b6cb0; }
---

# Fiscal North
## Market Analysis Slidedeck

**AI-Native Personal & Shared Household Finance for Europe**

July 2026 · Confidential

---

# Agenda

1. Executive Summary
2. Product & Market Definition
3. Market Size & Growth
4. Segmentation & Target Customers
5. Competitive Landscape
6. Shared Portfolio Opportunity
7. Trends & Drivers
8. Regulatory Environment
9. SWOT Analysis
10. Go-to-Market Strategy
11. Financial Outlook & KPIs
12. Recommendations & Roadmap

---

# Executive Summary

**Fiscal North** is a full-stack personal finance platform combining portfolio aggregation, budgeting, contracts, goals, PSD2 bank sync, and an AI financial assistant — with a planned **shared household portfolio** layer.

| Dimension | Assessment |
|-----------|------------|
| Market attractiveness | **High** — EU PFM ~$3.1B (2025), ~13% CAGR |
| AI sub-segment | **High growth** — ~$1.34B (2026), ~22% CAGR |
| Competitive intensity | High in solo aggregation; **low in DACH shared + AI** |
| Strategic wedge | **Household finance portfolio + AI copilot for Europe** |

---

# The Problem

Consumers manage money across **fragmented tools**:

- 3–5 bank accounts, depot, crypto, PayPal, insurance
- Subscriptions buried in statements
- Budgets in spreadsheets; goals in notes
- Couples share logins or maintain parallel trackers

**Result:** No single, trustworthy view of personal or household financial health — and rising cost-of-living makes this costly.

> Only ~18% of multi-adult households use collaborative budgeting tools today.

---

# The Solution — Fiscal North Today

| Capability | Status |
|------------|--------|
| Multi-account portfolio (checking, savings, depot, crypto, pension…) | ✅ Live |
| Transactions, budgets, contracts, goals | ✅ Live |
| PSD2 bank sync (Berlin Group XS2A / finAPI) | ✅ Premium |
| AI assistant with actionable proposals | ✅ Premium |
| CSV import, insights dashboard, notifications | ✅ Live |
| Multilingual (DE, EN, ES, FR) | ✅ Live |
| Self-hostable Docker deployment | ✅ Live |
| **Shared household portfolio** | 🔜 Planned |

---

# Market Definition

**Primary market:** Personal finance management (PFM) software — mobile & web apps for budgeting, aggregation, and financial planning.

**Adjacent markets included in this analysis:**

- Expense tracking & subscription management
- AI-assisted financial wellness
- Shared / couples / household budgeting
- Open-banking aggregation (PSD2 AISP)

**Geographic focus:** DACH first → broader EU

---

# Global Market Size

| Segment | 2025–2026 Baseline | Forecast | CAGR |
|---------|-------------------|----------|------|
| Personal finance apps (broad) | ~$25.8B (2026) | ~$167B (2035) | ~20.6% |
| PFM / budgeting tools (narrow) | ~$8.7B (2026) | ~$23B (2035) | ~11.4% |
| Expense tracker apps | ~$10B (2025) | ~$28.7B (2036) | ~10.1% |
| AI in personal finance | ~$1.1B (2025) | ~$2.95B (2030) | ~22% |

*Sources: Business Research Insights, MarkWide Research, FMI, GII Research*

---

# Europe & DACH — Our Beachhead

| Metric | Value |
|--------|-------|
| Europe PFM market (2025) | **~$3.1 billion** |
| Europe forecast (2033) | **~$10.7 billion** |
| Europe CAGR | **~12.9%** |
| Germany | Largest EU market; PSD2-native aggregation is table stakes |

**EU-specific drivers:**
- PSD2 open banking enables multi-bank aggregation
- Cost-of-living pressure increases budgeting demand
- GDPR creates trust expectations — and barriers for US-only apps

---

# Market Segmentation

| Segment | Size | Willingness to Pay | Fiscal North Fit |
|---------|------|-------------------|------------------|
| Solo budgeters (DACH) | Very large | €3–5/mo | ★★★★★ Strong today |
| Couples / households | Large, underserved | €5–10/mo | ★★★☆☆ → ★★★★★ with sharing |
| AI-curious 25–40 | Growing | €4–8/mo | ★★★★★ |
| Privacy-first users | Niche, vocal | €5–8/mo | ★★★★☆ (self-host) |
| Investors (depot/crypto) | Medium | €5–10/mo | ★★★☆☆ (partial) |
| Multilingual EU expats | Medium | €4–7/mo | ★★★★☆ |

**Primary ICP:** DACH couples & cohabiting partners (25–45) with multiple accounts and hybrid finances.

---

# Target Customer Profile

### Primary Persona — "Anna & Lukas" (Household)

- Ages 28–38, dual income, renting or first-time buyers
- 4–8 financial accounts combined; some separate, some joint
- Want visibility without surveillance
- Already use Finanzguru or spreadsheets — frustrated by lack of shared planning
- Will pay €6–8/mo for reliable sync + AI + partner invite

### Secondary Persona — "Solo Professional"

- Single, 25–35, tech-comfortable
- Wants AI to optimize budgets, catch subscriptions, plan savings goals
- Entry via free tier → Premium conversion

---

# Competitive Landscape — DACH Solo PFM

| Competitor | Price | Users / Scale | Strength | Weakness |
|------------|-------|---------------|----------|----------|
| **Finanzguru** | Free / ~€2.99 mo | ~1.5M MAU (DE) | Brand, 3,000+ banks, contracts | Cloud-only, weak AI, no sharing |
| **Outbank** | ~€3.99 mo | ~56K MAU (DE) | Local privacy, 4,500+ banks, depot | No free tier, weak Android, no AI |
| **Finanzfluss Copilot** | Freemium | ~62K MAU | Content + tools | Not full PFM |
| **MoneyMoney** | ~€30 one-time | Mac power users | FinTS direct, no cloud | Mac-only, no mobile |
| **YNAB** | ~€13.80 mo | Global | Methodology, 6 seats | USD-centric, weak PSD2 DACH |

---

# Competitive Landscape — Shared / Couples Finance

| Competitor | Price | Built for Couples? | EU PSD2? | AI? |
|------------|-------|-------------------|----------|-----|
| **Honeydue** | Free | ✅ Yes | ❌ US banks | ❌ |
| **Monarch Money** | ~$99/yr | ✅ Household | ❌ US-focused | Limited |
| **YNAB** | ~$109/yr | Partial (6 seats) | ❌ Weak EU | ❌ |
| **Goodbudget** | Freemium | Partial | ❌ Manual | ❌ |
| **Zeta** | — | Was couples-first | ❌ | ❌ Acquired by Acorns 2025 |
| **Fiscal North** | Freemium | 🔜 Planned | ✅ PSD2 | ✅ Gemini assistant |

**White space:** No leading product combines PSD2 + AI + shared household portfolio in DACH.

---

# Competitive Positioning Map

```
                    HIGH AI / PROACTIVE GUIDANCE
                              │
         Fiscal North (target)│        Starling / Neobank AI
                              │
    ──────────────────────────┼──────────────────────────
    INDIVIDUAL                │              HOUSEHOLD /
                              │              SHARED
         Finanzguru           │         Honeydue (stagnant)
         Outbank              │         Monarch (US)
         YNAB                 │
                              │
                    LOW AI / PASSIVE TRACKING
```

**Position to win:** Upper-right quadrant — household-ready, AI-native, EU-native.

---

# Shared Portfolio — Why It Matters

### Macro signals

- **23%** of married couples hold **no joint accounts** (up from 15% in 1996) — hybrid "yours/mine/ours" is the norm
- **~18%** of multi-adult households use collaborative budgeting tools — **82% untapped**
- Honeydue in **maintenance mode** since ~2022; Zeta **exit via acquisition**

### Jobs-to-be-done

| Job | Today | With shared portfolio |
|-----|-------|----------------------|
| "Can we afford vacation?" | Spreadsheet | Unified household cash-flow |
| "Who paid for groceries?" | Notes / Splitwise | In-app split tracking |
| "Don't see my personal spending" | Separate apps | Per-account privacy controls |
| "Are we on track for emergency fund?" | Ad hoc talks | Shared goal + AI nudges |

---

# Shared Portfolio — MVP Feature Set

| Feature | Priority | Rationale |
|---------|----------|-----------|
| Household entity + partner invites | **P0** | Foundation |
| Unified household dashboard | **P0** | Core value prop |
| Per-account visibility (full / balance / hidden) | **P0** | Adoption enabler |
| Shared budgets & goals | **P0** | Planning layer |
| Split expenses & settlements | P1 | vs. Splitwise |
| Transaction comments / chat | P1 | Honeydue proved demand |
| AI household insights | P1 | Differentiator |
| Individual ↔ household view toggle | P1 | Hybrid finance UX |

---

# Market Trends — Tailwinds

1. **PSD2 / open banking** — standardized EU account access
2. **Cost-of-living pressure** — budgeting & contract tracking surge
3. **AI adoption** — ~60–67% of Gen Z/Millennials open to AI for money
4. **Agentic finance** — assistants that propose & execute actions (Fiscal North already does this)
5. **Couples app consolidation** — Honeydue stagnation, Zeta exit
6. **Freemium dominance** — ~62% of expense-tracker revenue from freemium models

---

# Market Trends — Headwinds

1. **Incumbent network effects** — Finanzguru's 1.5M+ users & bank coverage
2. **Platform bundling** — Gemini ambient finance on Android; Starling agentic AI in banking
3. **Trust & regulation** — GDPR, AISP licensing, AI transparency (EU AI Act)
4. **Mobile-first expectation** — Fiscal North is web SPA today; native apps expected
5. **Free-tier anchoring** — Finanzguru free sets low WTP for basic aggregation
6. **Data privacy skepticism** — cloud AI requires clear consent & opt-out

---

# Regulatory Environment

| Regulation | Impact |
|------------|--------|
| **PSD2 / PSD3** | Required for bank sync; AISP via finAPI partnership |
| **GDPR** | Consent, export, delete; self-host as trust option |
| **EU AI Act** | Transparency for AI recommendations; human confirmation for actions ✅ |
| **BaFin / national regulators** | Compliance via licensed aggregator |
| **Consumer protection** | AI must not present as licensed investment advice |

**Shared portfolio adds:** explicit consent per shared account, invite audit trail, data boundaries between members.

---

# SWOT Analysis

| **Strengths** | **Weaknesses** |
|---------------|----------------|
| Full portfolio account types | No shared features yet |
| AI assistant with actions | Limited brand awareness |
| PSD2 integration | Web-only (no native mobile) |
| Freemium + Stripe billing | Bank coverage TBD vs. Finanzguru |
| Multilingual, self-hostable | No live depot sync yet |

| **Opportunities** | **Threats** |
|-------------------|-------------|
| Shared household in DACH | Finanzguru free tier |
| AI household insights | Neobank embedded AI |
| Privacy / self-host positioning | Google ambient finance |
| Couples app consolidation | PSD2 regulatory cost |

---

# Pricing & Monetization Benchmarks

| Product | Individual | Household |
|---------|-----------|-----------|
| Finanzguru Plus | ~€2.99/mo (~€36/yr) | N/A |
| Outbank Pro | ~€3.99/mo (~€36/yr) | N/A |
| YNAB | ~€13.80/mo (~€109/yr) | Up to 6 users |
| Monarch Money | ~$99.99/yr | 2 people included |
| Honeydue | Free | Free (ads) |

### Fiscal North proposed tiers

| Plan | Price | Includes |
|------|-------|----------|
| **Free** | €0 | Manual tracking, CSV, core dashboard |
| **Premium** | €4.99/mo · €49/yr | AI, PSD2 sync, notifications |
| **Household Premium** | €7.99/mo · €79/yr | Premium + 2 seats, shared dashboard |
| **Household Plus** | €11.99/mo · €119/yr | + splits, AI household insights, 4 seats |

---

# Go-to-Market — Phase 1 (0–6 mo)

**Objective:** Solidify solo product & conversion funnel

- Expand PSD2 bank coverage; stabilize sync reliability
- Ship PWA or native mobile with push notifications
- Publish privacy/security page (GDPR, data location, AI opt-out)
- Launch content SEO: "Haushaltsbudget App", "Finanzen als Paar"
- Referral program: invite friend → extended trial
- Target **€3.99–4.99/mo** Premium to match Finanzguru Plus

**Channels:** Product Hunt, Reddit (r/Finanzen), Finanz-Influencer, App Store (when mobile ships)

---

# Go-to-Market — Phase 2 (6–12 mo)

**Objective:** Launch shared household portfolio

- Household entity, 2-seat Premium, shared dashboard, privacy controls
- Marketing: *"The first AI finance assistant for how European couples actually manage money"*
- Partner invite flow with dual onboarding
- PR angle: Honeydue gap + PSD2 + AI
- B2B2C pilots: financial coaches, employee wellness programs

**Target metrics:** 15% of Premium users invite a partner within 90 days

---

# Go-to-Market — Phase 3 (12–18 mo)

**Objective:** AI household intelligence & expansion

- Combined cash-flow forecasting for households
- Contract deduplication across partners
- Proactive "money date" AI summaries
- Expand to AT/CH/FR/NL/ES (languages already supported)
- Optional self-hosted household mode for privacy segment
- Explore white-label for credit unions / neobank partners

---

# Financial Outlook — 5-Year Model (Illustrative)

| Year | Free Users | Paying Users | ARPU/mo | ARR |
|------|-----------|--------------|---------|-----|
| Y1 | 5,000 | 250 | €5.50 | ~€16K |
| Y2 | 25,000 | 2,000 | €6.00 | ~€144K |
| Y3 | 80,000 | 8,000 | €6.50 | ~€624K |
| Y4 | 200,000 | 22,000 | €7.00 | ~€1.85M |
| Y5 | 450,000 | 55,000 | €7.50 | ~€4.95M |

**Assumptions:** 5–12% free→paid conversion; 35% Household tier attach by Y3; 4% monthly churn on paid.

*Conservative vs. Finanzguru scale; aggressive vs. typical early-stage SaaS.*

---

# Unit Economics (Target State)

| Metric | Target |
|--------|--------|
| CAC (blended) | €15–25 |
| LTV (24-mo paid) | €120–160 |
| LTV:CAC | **>5:1** |
| Gross margin | **>80%** (SaaS + API costs) |
| Payback period | <6 months |
| Household cohort retention | **2× solo** (industry benchmark) |

**Cost drivers:** finAPI/PSD2 per-connection fees, Gemini API usage, hosting (scales with users).

---

# Key Performance Indicators

| Category | KPI |
|----------|-----|
| Acquisition | CAC, organic vs. paid, partner-invite rate |
| Activation | % linking ≥1 bank; time to first budget/goal |
| Shared portfolio | % households with 2+ members; privacy config rate |
| Engagement | WAU/MAU, AI sessions/user, notification open rate |
| Revenue | Free→Premium conversion, Household attach, ARPU, churn |
| Retention | 90-day retention solo vs. household |

---

# Risk Matrix

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Finanzguru adds sharing | Medium | High | Speed to market; AI depth |
| PSD2 API cost overrun | Medium | Medium | Usage caps; Premium gating |
| Low conversion on free | High | High | Onboarding, AI wow-moment, trials |
| AI trust concerns | Medium | Medium | Transparency, opt-out, no auto-spend |
| Mobile delay | Medium | High | PWA interim; prioritize iOS |
| Regulatory change (PSD3) | Low | Medium | Aggregator partnership |

---

# Strategic Recommendations

1. **Prioritize shared household portfolio** — largest defensible wedge in DACH
2. **Ship mobile (PWA → native)** — table stakes for couples use case
3. **Double down on AI actions** — move from chatbot to agentic assistant
4. **Price Household at €7.99/mo** — below 2× solo, above Finanzguru Plus
5. **Build trust narrative** — GDPR, self-host option, AI opt-out, German servers
6. **Partner with finAPI/Tink** — breadth of bank coverage vs. build
7. **Measure household retention** — primary north-star for product-market fit

---

# 18-Month Product Roadmap

```
Q3 2026  │ Mobile PWA · Bank coverage expansion · Privacy page
Q4 2026  │ Household MVP (invites, shared dashboard, privacy)
Q1 2027  │ Shared budgets/goals · Split expenses · AI household insights
Q2 2027  │ Native iOS/Android · AT/CH launch · B2B2C pilots
Q3 2027  │ Depot live sync · Household Plus tier · Referral program v2
Q4 2027  │ Agentic workflows · White-label exploration
```

---

# Summary

| Question | Answer |
|----------|--------|
| Is the market large enough? | **Yes** — $3.1B EU PFM, growing ~13%/yr |
| Is there a gap? | **Yes** — no DACH-native shared + AI + PSD2 product |
| Can Fiscal North win? | **Yes**, with household sharing + AI depth + EU trust |
| What to build first? | Mobile + household MVP + conversion funnel |
| What to avoid? | Competing on free solo aggregation alone |

---

# Thank You

**Fiscal North**
*Your finance assistant — personal portfolio, shared household, AI-guided.*

Contact: [Insert contact]
Website: [Insert URL]
GitHub: github.com/Hinnisiemsen/FiscalNorth

---

# Appendix A — Data Sources

- HTF Market Insights — Europe Personal Finance Apps Market
- Future Market Insights — Expense Tracker Apps Market
- Business Research Insights — Personal Finance App Market
- GII Research — AI for Personal Finance Global Market Report 2026
- Sensor Tower — Top Consumer Finance Apps Germany Q4 2025
- BestMoney — Budgeting Apps for Couples 2026
- VikoFintech — Haushaltsbuch-Apps 2026
- Finanzguru vs. Outbank comparative analyses (neuebanken.de, ftd.de)

---

# Appendix B — Fiscal North Feature Inventory

**Free tier:** Accounts, transactions, budgets, goals, categories, contracts, CSV import, dashboard insights

**Premium tier:** AI assistant, AI goal planner, PSD2 bank sync, proactive AI notifications (budget alerts, optimization, insights cron jobs)

**Tech stack:** Java 21 / Spring Boot 3.3 · Angular 20 · PostgreSQL · RabbitMQ/Kafka · Gemini AI · Stripe · finAPI XS2A

**Deployment:** Docker Compose; self-hostable; CI/CD via GitHub Actions
