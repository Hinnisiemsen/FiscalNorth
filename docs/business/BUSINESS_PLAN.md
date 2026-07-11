# Fiscal North — Business Plan

**Version:** 1.0  
**Date:** July 2026  
**Confidential**

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Company Overview](#2-company-overview)
3. [Problem Statement](#3-problem-statement)
4. [Solution & Product](#4-solution--product)
5. [Market Analysis](#5-market-analysis)
6. [Competitive Analysis](#6-competitive-analysis)
7. [Shared Household Portfolio Strategy](#7-shared-household-portfolio-strategy)
8. [Business Model & Pricing](#8-business-model--pricing)
9. [Go-to-Market Strategy](#9-go-to-market-strategy)
10. [Operations & Technology](#10-operations--technology)
11. [Regulatory & Compliance](#11-regulatory--compliance)
12. [Team & Organization](#12-team--organization)
13. [Financial Plan](#13-financial-plan)
14. [Risk Analysis & Mitigation](#14-risk-analysis--mitigation)
15. [Milestones & Roadmap](#15-milestones--roadmap)
16. [Appendices](#16-appendices)

---

## 1. Executive Summary

### Mission

Fiscal North empowers individuals and households across Europe to understand, plan, and optimize their finances through a unified portfolio view, optional shared household access, and an AI assistant that turns insight into action.

### Vision

Become the leading AI-native household finance platform in Europe — the place where couples and families see "yours, mine, and ours" in one trusted dashboard.

### The Opportunity

The European personal finance app market is valued at approximately **$3.1 billion (2025)** and projected to reach **$10.7 billion by 2033** (~12.9% CAGR). Within this market, three converging trends create a window for Fiscal North:

1. **Open banking maturity (PSD2)** — multi-bank aggregation is now technically feasible and expected by consumers.
2. **AI in finance** — a $1.34B segment growing at ~22% CAGR, shifting from chatbots to agentic assistants.
3. **Shared household finance gap** — only ~18% of multi-adult households use collaborative budgeting tools; leading couples apps (Honeydue) are stagnant or exiting (Zeta → Acorns).

No product today combines **PSD2-native aggregation + AI action assistant + shared household portfolio** for the DACH/EU market.

### Product Status

Fiscal North is a **working full-stack product** (not a prototype):

- Spring Boot backend + Angular 20 frontend
- Account portfolio across 12+ account types (checking, savings, depot, crypto, pension, etc.)
- Budgets, contracts, goals, transactions, CSV import, insights dashboard
- PSD2 bank sync via Berlin Group XS2A (finAPI)
- Gemini-powered AI assistant with confirmable action proposals
- Stripe freemium billing with 14-day trial
- Multilingual (DE, EN, ES, FR); self-hostable via Docker

**Next major product milestone:** Shared household portfolio (Q4 2026).

### Business Model

Freemium SaaS with tiered subscriptions:

| Tier | Price | Target |
|------|-------|--------|
| Free | €0 | Acquisition & habit formation |
| Premium | €4.99/mo | Solo power users |
| Household Premium | €7.99/mo | Couples & cohabiting partners |
| Household Plus | €11.99/mo | Families & advanced sharing |

### Financial Highlights (5-Year Projection)

| Year | Paying Users | ARR |
|------|-------------|-----|
| Y1 | 250 | ~€16K |
| Y2 | 2,000 | ~€144K |
| Y3 | 8,000 | ~€624K |
| Y4 | 22,000 | ~€1.85M |
| Y5 | 55,000 | ~€4.95M |

### Funding Ask

**€500K–750K seed** to fund mobile development, household features, and DACH go-to-market over 18 months, targeting 8,000 paying users and €624K ARR run-rate.

---

## 2. Company Overview

### Legal Structure

- **Entity:** [To be incorporated — suggest German GmbH or UG for DACH credibility]
- **Founder:** Hinni Siemsen
- **Location:** Germany (DACH focus)
- **Repository:** github.com/Hinnisiemsen/FiscalNorth

### Company History

Fiscal North was conceived and built as an open-source-capable personal finance platform addressing gaps in existing DACH tools: lack of AI guidance, no household sharing, and limited self-hosting options. The product reached MVP completeness for solo users in 2025–2026, including production-grade infrastructure (Docker, CI/CD, Stripe billing, PSD2 integration).

### Values

1. **Transparency** — users control what they share, with whom, and can opt out of AI
2. **Privacy** — GDPR-first; optional self-hosting for trust-sensitive users
3. **Action over analytics** — AI that proposes concrete steps, not just charts
4. **Household-realistic** — designed for hybrid "yours/mine/ours" finances, not outdated joint-account assumptions

---

## 3. Problem Statement

### For Individuals

Modern consumers manage money across an average of **4–8 financial accounts**: checking, savings, credit cards, investment depot, crypto wallets, PayPal, insurance policies, and pension products. Each institution provides its own app; no single view exists for:

- Total net worth and cash-flow
- Subscription and contract commitments
- Budget adherence across categories
- Progress toward savings goals

Existing DACH apps (Finanzguru, Outbank) solve aggregation for **one person** but offer limited AI guidance and no path toward household planning.

### For Households

Financial management is a **household activity**, but software treats it as individual:

- **23%** of married couples hold no joint bank accounts (up from 15% in 1996) — hybrid finances are the norm
- Couples resort to shared logins, spreadsheets, or US-only apps (Honeydue, Monarch) with poor EU bank support
- Money is the **#2 source of relationship conflict** — yet only ~18% of multi-adult households use collaborative budgeting tools
- Leading couples apps are in **maintenance or exit** (Honeydue since ~2022; Zeta acquired by Acorns in 2025)

### Market Pain Summary

| Pain | Current Workaround | Cost |
|------|-------------------|------|
| Fragmented accounts | Multiple banking apps | Time, missed subscriptions |
| No household view | Spreadsheets, shared logins | Relationship friction |
| Passive tracking | Manual categorization | Abandonment after 2–3 months |
| Privacy vs. transparency | Separate apps entirely | Incomplete picture |
| Rising fixed costs | None / mental math | Overspending, anxiety |

---

## 4. Solution & Product

### Value Proposition

**Fiscal North = Personal portfolio + Shared household (planned) + AI copilot**

One platform to see all accounts, track spending and contracts, set budgets and goals, sync banks via PSD2, and get AI recommendations that create budgets, categories, transactions, and goals — with optional partner sharing and granular privacy controls.

### Current Feature Set

#### Free Tier
- Unlimited manual accounts (12+ types including depot, crypto, pension)
- Transaction entry, transfers, split bookings, categorization
- Budgets with usage tracking
- Financial goals with progress tracking
- Recurring contract/subscription management
- CSV transaction import
- Dashboard with KPIs, category breakdown, monthly trends
- Custom categories
- Multilingual UI (DE, EN, ES, FR)

#### Premium Tier (Stripe Subscription)
- **AI Assistant** — natural-language chat with full financial context; proposes confirmable actions (create budget, category, transaction, goal)
- **AI Goal Planner** — guided interview to build savings plans
- **PSD2 Bank Sync** — connect bank accounts via finAPI XS2A; automatic transaction import
- **AI Notifications** — proactive cron-driven alerts (budget thresholds, financial insights, optimization suggestions)

#### Infrastructure
- Self-hostable Docker Compose stack (PostgreSQL, RabbitMQ, backend, frontend)
- Production deployment via GitHub Actions CI/CD
- OAuth2 (Google) + email/password authentication
- Server-side entitlement enforcement

### Planned: Shared Household Portfolio (Q4 2026)

| Feature | Description |
|---------|-------------|
| Household entity | Create or join a household; invite partner via email |
| Shared dashboard | Combined net worth, income, expenses, KPIs |
| Privacy controls | Per account: share all transactions, balance only, or hide |
| Shared budgets & goals | Joint planning with individual contribution tracking |
| Split expenses | Track who paid; settlement suggestions |
| Transaction comments | In-context discussion on specific transactions |
| AI household insights | "Combined fixed costs are 38% of household income" |
| View toggle | Switch between "My finances" and "Our household" |

### Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.3.5, Spring Data JPA, Spring Security |
| API | Spring WebFlux, Spring Data REST |
| Frontend | Angular 20, TypeScript |
| Database | PostgreSQL (production), H2 (dev) |
| Messaging | RabbitMQ, Apache Kafka |
| AI | Google Gemini via Spring AI |
| Bank sync | Berlin Group XS2A (finAPI client) |
| Billing | Stripe Checkout + Customer Portal + webhooks |
| Deployment | Docker, nginx, GitHub Actions |

---

## 5. Market Analysis

### 5.1 Market Size

#### Global

| Segment | 2025–2026 | Forecast | CAGR | Source |
|---------|-----------|----------|------|--------|
| Personal finance apps | ~$25.8B (2026) | ~$167B (2035) | ~20.6% | Business Research Insights |
| PFM / budgeting tools | ~$8.7B (2026) | ~$23B (2035) | ~11.4% | MarkWide Research |
| Expense tracker apps | ~$10B (2025) | ~$28.7B (2036) | ~10.1% | Future Market Insights |
| AI in personal finance | ~$1.34B (2026) | ~$2.95B (2030) | ~22% | GII Research |

#### Europe

| Metric | Value |
|--------|-------|
| Europe PFM market (2025) | ~$3.1 billion |
| Europe forecast (2033) | ~$10.7 billion |
| CAGR | ~12.9% |

#### Serviceable Addressable Market (SAM)

DACH + multilingual EU (DE, EN, ES, FR): estimated **~$800M** based on DACH representing ~25% of EU fintech adoption with highest ARPU.

#### Serviceable Obtainable Market (SOM)

DACH couples + AI-curious solo professionals: **~$120M** addressable slice over 5 years (conservative 1.5% of SAM).

### 5.2 Market Segmentation

| Segment | Description | Size | WTP |
|---------|-------------|------|-----|
| Solo budgeters | Individual account aggregation & budgeting | Very large | €3–5/mo |
| Couples / households | Shared visibility + joint planning | Large, underserved | €5–10/mo |
| AI-curious professionals | Want proactive guidance, not just charts | Growing | €4–8/mo |
| Privacy-first | Distrust cloud storage; want local/self-host | Niche, vocal | €5–8/mo |
| Investors | Depot + crypto tracking alongside cash flow | Medium | €5–10/mo |

### 5.3 Target Customer Profiles

**Primary ICP — "Anna & Lukas" (Household)**
- Ages 28–38, dual income, urban DACH
- 4–8 accounts combined; hybrid finance model
- Currently uses Finanzguru individually or a spreadsheet together
- Pain: no shared view, no shared goal tracking, privacy concerns with full sharing
- WTP: €6–8/mo for household tier

**Secondary ICP — "Solo Professional"**
- Age 25–35, single, tech-comfortable
- Subscription-heavy, savings-oriented
- Entry: free tier → Premium via AI assistant "wow moment"
- WTP: €4–5/mo

### 5.4 Market Trends

**Tailwinds:**
- PSD2 open banking lowering aggregation barriers
- Cost-of-living crisis driving budgeting urgency
- AI adoption among millennials/Gen Z (~60–67% open to AI for money)
- Agentic finance shift (Starling, Google Gemini) validating category
- Couples app consolidation (Honeydue stagnation, Zeta exit)
- Freemium model dominance (~62% of expense tracker revenue)

**Headwinds:**
- Finanzguru's 1.5M+ MAU and free tier anchoring
- Platform bundling (neobank AI, Gemini ambient finance on Android)
- Mobile-first consumer expectations
- GDPR/AI Act compliance costs
- PSD2 API per-connection costs

---

## 6. Competitive Analysis

### 6.1 DACH Solo PFM

| Competitor | Price | MAU (DE) | Strengths | Weaknesses |
|------------|-------|----------|-----------|------------|
| Finanzguru | Free / €2.99 mo | ~1.5M | Brand, 3,000+ banks, contract detection | No AI, no sharing, cloud-only |
| Outbank | €3.99 mo | ~56K | Local privacy, 4,500+ banks, depot | No free tier, no AI, weak Android |
| Finanzfluss Copilot | Freemium | ~62K | Content ecosystem | Not full PFM |
| MoneyMoney | €30 + €15/yr | Niche | FinTS direct, macOS power | Mac-only, no mobile, no AI |
| YNAB | €13.80 mo | Global | Methodology, 6 users | USD-centric, weak EU PSD2 |

### 6.2 Shared / Couples Finance (Global)

| Competitor | Price | EU PSD2 | AI | Status |
|------------|-------|---------|-----|--------|
| Honeydue | Free | ❌ | ❌ | Maintenance mode |
| Monarch Money | ~$99/yr | ❌ | Limited | US-focused |
| YNAB | ~$109/yr | ❌ | ❌ | Methodology-first |
| Goodbudget | Freemium | ❌ | ❌ | Manual envelopes |
| Zeta | — | ❌ | ❌ | Acquired by Acorns 2025 |

### 6.3 Competitive Advantages

1. **AI action assistant** — proposes and validates budgets, goals, categories, transactions (not just Q&A)
2. **Household roadmap** — no DACH competitor building shared portfolio + AI
3. **Self-hostable** — unique among cloud PFM apps; appeals to privacy segment
4. **Multilingual** — 4 languages ready for EU expansion
5. **Full-stack ownership** — no dependency on third-party frontend; rapid iteration

### 6.4 Competitive Risks

- Finanzguru adds household sharing (medium likelihood, high impact)
- Neobanks embed agentic AI (Starling already launched Gemini assistant)
- Google Gemini ambient expense tracking on Android (platform risk)

---

## 7. Shared Household Portfolio Strategy

### 7.1 Strategic Rationale

Shared household portfolio is Fiscal North's **primary defensible wedge**. Solo aggregation competes directly with Finanzguru's free tier (1.5M users). Household sharing creates:

- **2× retention** (industry benchmark for multi-user SaaS)
- **Higher ARPU** (Household tier at €7.99 vs. €4.99 solo)
- **Viral acquisition** (partner invite = zero-CAC second user)
- **Switching costs** (shared history, goals, budgets)

### 7.2 Product Principles

1. **Privacy by default** — new accounts hidden from partner until explicitly shared
2. **Granular control** — per-account visibility: full transactions, balance only, or hidden
3. **No surveillance UX** — frame as "our household" not "monitor your partner"
4. **Individual + household views** — always allow "my finances" toggle
5. **AI for the household** — insights based on combined data with consent

### 7.3 MVP Scope (Q4 2026)

- Household creation and email invite
- Accept/decline invite flow
- Shared dashboard (combined balance, income, expenses, category breakdown)
- Per-account visibility settings
- Shared budgets and shared goals
- Premium gating: Household tier requires at least one Premium subscriber

### 7.4 Phase 2 (Q1–Q2 2027)

- Split expense tracking and settlement
- Transaction-level comments
- AI household insights and "money date" summary notifications
- Household Plus tier (4 seats, splits, advanced AI)

---

## 8. Business Model & Pricing

### 8.1 Revenue Model

Primary: **subscription SaaS** (monthly and annual via Stripe)

Future revenue streams:
- **B2B2C:** employer financial wellness benefit (€2–3/employee/mo)
- **Affiliate:** insurance/subscription comparison (following Finanzguru model)
- **White-label:** licensed platform for credit unions or neobanks

### 8.2 Pricing Tiers

| Tier | Monthly | Annual | Features |
|------|---------|--------|----------|
| Free | €0 | €0 | Manual tracking, CSV, dashboard, budgets, goals, contracts |
| Premium | €4.99 | €49 (18% discount) | + AI assistant, PSD2 sync, AI notifications, goal planner |
| Household Premium | €7.99 | €79 | + 2 seats, shared dashboard, privacy controls, shared budgets/goals |
| Household Plus | €11.99 | €119 | + 4 seats, split expenses, AI household insights |

- 14-day free trial on all paid tiers (existing Stripe configuration)
- 3-day past-due grace period (existing configuration)

### 8.3 Unit Economics (Target at Scale)

| Metric | Target |
|--------|--------|
| Blended ARPU | €6.50/mo (Y3) |
| CAC (blended) | €15–25 |
| LTV (24-month) | €120–160 |
| LTV:CAC | >5:1 |
| Gross margin | >80% |
| Monthly churn (paid) | <4% |
| Free → paid conversion | 8–12% (Y2–Y3) |
| Household tier attach | 35% of paid (Y3) |

### 8.4 Cost Structure

| Cost Item | Type | Notes |
|-----------|------|-------|
| finAPI / PSD2 API | Variable | Per-connection/month; gated behind Premium |
| Gemini API | Variable | Per AI session; scales with Premium users |
| Cloud hosting | Semi-variable | PostgreSQL, RabbitMQ, compute |
| Stripe fees | Variable | ~2.9% + €0.25 per transaction |
| Salaries | Fixed | Post-seed: 3–5 FTE |
| Marketing | Variable | Performance + content |

---

## 9. Go-to-Market Strategy

### 9.1 Phase 1: Solo Product & Conversion (Months 0–6)

**Goal:** 5,000 free users, 250 paid users

| Channel | Tactic |
|---------|--------|
| Content SEO | "Haushaltsbudget App", "Finanzen als Paar", "PSD2 Finanz-App" |
| Community | Reddit r/Finanzen, r/EuropeFIRE, Finanz-Influencer |
| Product Hunt | Launch with AI assistant angle |
| Referral | Invite friend → 30-day Premium trial extension |
| PR | "Open-source-capable AI finance app from Germany" |

**Product requirements:** Mobile PWA, bank coverage expansion, privacy landing page

### 9.2 Phase 2: Household Launch (Months 6–12)

**Goal:** 25,000 free users, 2,000 paid, 15% on Household tier

| Channel | Tactic |
|---------|--------|
| Partner invite virality | In-app "Invite your partner" with dual trial |
| PR | "The Honeydue for Europe — but with PSD2 and AI" |
| Influencer | Couples finance / relationship content creators |
| Paid social | Instagram/TikTok targeting 25–40 DACH couples |
| Partnerships | Financial coaches, relationship counselors |

**Product requirements:** Household MVP, shared dashboard, privacy controls

### 9.3 Phase 3: Scale & Expand (Months 12–18)

**Goal:** 80,000 free users, 8,000 paid, €624K ARR

| Channel | Tactic |
|---------|--------|
| App Store / Play Store | Native mobile launch |
| Geographic expansion | AT, CH, FR, ES (languages ready) |
| B2B2C pilots | Employee wellness programs |
| Affiliate | Subscription/insurance comparison integration |

### 9.4 Key Metrics

| Metric | Y1 Target | Y3 Target |
|--------|-----------|-----------|
| Free users | 5,000 | 80,000 |
| Paying users | 250 | 8,000 |
| Conversion rate | 5% | 10% |
| Household attach | — | 35% |
| 90-day retention (paid) | 60% | 75% |
| Partner invite rate | — | 15% of Premium |
| NPS | >30 | >45 |

---

## 10. Operations & Technology

### 10.1 Architecture

Fiscal North runs as a containerized micro-monolith:

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│  Angular 20  │────▶│  nginx proxy  │────▶│ Spring Boot │
│  Frontend    │     │  (port 3000)  │     │  Backend    │
└─────────────┘     └──────────────┘     └──────┬──────┘
                                                 │
                    ┌──────────────┬─────────────┼──────────┐
                    ▼              ▼             ▼          ▼
              PostgreSQL      RabbitMQ      finAPI     Gemini
              (data)          (events)      (PSD2)     (AI)
```

### 10.2 Security

- Spring Security with OAuth2 + session cookies
- CSRF protection on mutating endpoints
- Server-side entitlement enforcement (not client-side paywalls)
- Stripe webhook signature verification with idempotent event processing
- User data isolation by owner_id on all entities

### 10.3 Scalability Plan

| Users | Infrastructure |
|-------|---------------|
| 0–10K | Single VPS (current deploy model) |
| 10K–100K | Managed PostgreSQL, horizontal backend replicas |
| 100K+ | Kubernetes, read replicas, CDN for frontend, API rate limiting |

### 10.4 Development Process

- GitHub Actions CI: Maven tests (Testcontainers), Angular build + Karma, Docker smoke test
- Auto-deploy to production on merge to master (when DEPLOY_ENABLED=true)
- Feature branches with PR review

---

## 11. Regulatory & Compliance

### 11.1 Applicable Regulations

| Regulation | Requirement | Fiscal North Approach |
|------------|-------------|----------------------|
| GDPR | Data protection, consent, portability | Privacy policy, export/delete, consent flows |
| PSD2 | Account access via licensed AISP | Partnership with finAPI (licensed aggregator) |
| EU AI Act | Transparency for AI systems | AI opt-out, human confirmation for actions, no auto-spending |
| ePrivacy | Cookie consent | Session cookies only; minimal tracking |
| Consumer protection | No misleading financial advice | Disclaimers; AI is informational, not advisory |

### 11.2 Shared Portfolio Compliance

- Explicit consent before sharing any account with household member
- Audit log of invite, accept, visibility changes
- Ability to leave household and revoke shared access instantly
- Data minimization: share only what user configures

### 11.3 Future Requirements

- BaFin notification if payment initiation added
- ISO 27001 consideration at 100K+ users
- SOC 2 for B2B2C enterprise sales

---

## 12. Team & Organization

### 12.1 Current Team

| Role | Person | Responsibility |
|------|--------|---------------|
| Founder & Full-Stack Developer | Hinni Siemsen | Product, backend, frontend, infra, AI integration |

### 12.2 Hiring Plan (Post-Seed)

| Role | Timing | Priority |
|------|--------|----------|
| Mobile Engineer (iOS/Android or Flutter) | Month 1–3 | Critical |
| Growth Marketer (DACH fintech) | Month 3–6 | High |
| Backend Engineer | Month 6–9 | Medium |
| Part-time Compliance/Legal | Month 1 | High |
| Customer Success (part-time) | Month 9–12 | Medium |

### 12.3 Advisory Needs

- Fintech regulatory advisor (PSD2/AISP)
- DACH growth advisor (Finanzguru-era marketing)
- AI/ML advisor (agentic assistant roadmap)

### 12.4 Organization Chart (18-Month Target)

```
CEO / Founder (Hinni Siemsen)
├── Engineering (2–3)
│   ├── Mobile Engineer
│   └── Backend Engineer
├── Growth (1)
│   └── DACH Marketing Lead
└── Operations (0.5)
    └── Compliance / Legal (part-time)
```

---

## 13. Financial Plan

### 13.1 Revenue Projections (5-Year)

| | Year 1 | Year 2 | Year 3 | Year 4 | Year 5 |
|--|--------|--------|--------|--------|--------|
| Free users (EOY) | 5,000 | 25,000 | 80,000 | 200,000 | 450,000 |
| Paying users (EOY) | 250 | 2,000 | 8,000 | 22,000 | 55,000 |
| Conversion rate | 5% | 8% | 10% | 11% | 12% |
| Blended ARPU/mo | €5.50 | €6.00 | €6.50 | €7.00 | €7.50 |
| **ARR (EOY)** | **€16K** | **€144K** | **€624K** | **€1.85M** | **€4.95M** |
| YoY growth | — | 800% | 333% | 196% | 168% |

### 13.2 Revenue Mix (Year 3 Target)

| Tier | % of Paid Users | ARPU/mo | Revenue Share |
|------|----------------|---------|---------------|
| Premium (solo) | 65% | €4.99 | ~40% |
| Household Premium | 25% | €7.99 | ~35% |
| Household Plus | 10% | €11.99 | ~25% |

### 13.3 Expense Projections (5-Year)

| Category | Y1 | Y2 | Y3 | Y4 | Y5 |
|----------|----|----|----|----|-----|
| Personnel | €0 | €120K | €360K | €720K | €1.2M |
| Infrastructure & API | €5K | €25K | €80K | €200K | €450K |
| Marketing | €10K | €60K | €180K | €400K | €800K |
| Legal & compliance | €5K | €15K | €30K | €50K | €80K |
| Other (office, tools) | €3K | €15K | €40K | €80K | €120K |
| **Total OpEx** | **€23K** | **€235K** | **€690K** | **€1.45M** | **€2.65M** |

*Y1 assumes pre-seed/bootstrapped; Y2+ assumes seed funding deployed.*

### 13.4 Profitability

| | Y1 | Y2 | Y3 | Y4 | Y5 |
|--|----|----|----|----|-----|
| Revenue | €8K | €86K | €420K | €1.3M | €3.7M |
| OpEx | €23K | €235K | €690K | €1.45M | €2.65M |
| **Net** | **-€15K** | **-€149K** | **-€270K** | **-€150K** | **+€1.05M** |

Break-even projected in **Year 5** at ~55K paying users. Earlier break-even possible with B2B2C revenue or higher conversion.

### 13.5 Funding Requirements

**Seed Round: €500K–750K**

| Use of Funds | % | Amount (€625K mid) |
|--------------|---|-------------------|
| Engineering (mobile + household) | 45% | €281K |
| Go-to-market & growth | 30% | €188K |
| Infrastructure & API costs | 10% | €63K |
| Legal, compliance, ops | 10% | €63K |
| Buffer | 5% | €31K |

**Runway:** 18 months to €624K ARR run-rate (8K paying users)

**Series A trigger:** €500K+ ARR, proven household retention uplift, native mobile apps live

---

## 14. Risk Analysis & Mitigation

| # | Risk | Likelihood | Impact | Mitigation |
|---|------|-----------|--------|------------|
| 1 | Finanzguru adds household sharing | Medium | High | Speed to market; differentiate on AI depth |
| 2 | Low free→paid conversion | High | High | AI wow-moment onboarding; 14-day trial; referral |
| 3 | PSD2 API costs exceed revenue | Medium | Medium | Gate sync behind Premium; usage monitoring |
| 4 | Mobile delay loses users | Medium | High | PWA interim; prioritize iOS |
| 5 | AI trust / privacy backlash | Medium | Medium | Opt-out, transparency, no auto-spend, self-host option |
| 6 | Neobank platform bundling | Medium | High | Standalone value: multi-bank, multi-household, self-host |
| 7 | Google ambient finance on Android | Low | High | Depth over convenience; household features |
| 8 | Regulatory change (PSD3, AI Act) | Low | Medium | Aggregator partnership; legal advisor |
| 9 | Key person dependency (solo founder) | High | High | Seed enables first hires; document architecture |
| 10 | Churn after trial | Medium | Medium | Household invite during trial; engagement notifications |

---

## 15. Milestones & Roadmap

### 15.1 Product Roadmap

| Quarter | Milestone |
|---------|-----------|
| Q3 2026 | Mobile PWA · Bank coverage expansion · Privacy/security page |
| Q4 2026 | **Household MVP** — invites, shared dashboard, privacy controls |
| Q1 2027 | Shared budgets/goals · Split expenses · AI household insights |
| Q2 2027 | Native iOS/Android apps · AT/CH market launch |
| Q3 2027 | Depot live sync · Household Plus tier · Referral program v2 |
| Q4 2027 | Agentic workflows · B2B2C pilot · White-label exploration |

### 15.2 Business Milestones

| Date | Milestone |
|------|-----------|
| Q3 2026 | Seed round close |
| Q4 2026 | 1,000 free users · 100 paid |
| Q1 2027 | Household launch · first 50 household accounts |
| Q2 2027 | Mobile app store launch · 500 paid |
| Q4 2027 | 8,000 paid · €624K ARR · Series A ready |

---

## 16. Appendices

### Appendix A: Document Index

| Document | Location | Purpose |
|----------|----------|---------|
| Market Analysis Slidedeck | `docs/business/MARKET_ANALYSIS_SLIDEDECK.md` | Detailed market slides (Marp) |
| Pitch Deck | `docs/business/PITCH_DECK.md` | Investor presentation (Marp) |
| This Business Plan | `docs/business/BUSINESS_PLAN.md` | Comprehensive strategy document |
| Technical README | `README.md` | Product & architecture overview |
| Billing Setup | `docs/BILLING.md` | Stripe configuration |
| Auth Documentation | `docs/AUTH.md` | Authentication & user isolation |

### Appendix B: Rendering Slide Decks

Both slide decks use [Marp](https://marp.app/) format. To render as PDF or PowerPoint:

```bash
# Install Marp CLI
npm install -g @marp-team/marp-cli

# Export market analysis
marp docs/business/MARKET_ANALYSIS_SLIDEDECK.md --pdf -o market-analysis.pdf

# Export pitch deck
marp docs/business/PITCH_DECK.md --pdf -o pitch-deck.pdf

# Or export as PowerPoint
marp docs/business/PITCH_DECK.md --pptx -o pitch-deck.pptx
```

VS Code users can install the "Marp for VS Code" extension for live preview.

### Appendix C: Key Data Sources

1. HTF Market Insights — Europe Personal Finance Apps Market (2025–2033)
2. Future Market Insights — Expense Tracker Apps Market (2025–2036)
3. Business Research Insights — Personal Finance App Market (2026–2035)
4. GII Research — AI for Personal Finance Global Market Report 2026
5. Sensor Tower — Top Consumer Finance Apps Germany Q4 2025
6. BestMoney — Budgeting Apps for Couples 2026
7. VikoFintech — Haushaltsbuch-Apps Budgetplanung 2026
8. neuebanken.de / ftd.de — Finanzguru vs. Outbank comparisons

### Appendix D: Glossary

| Term | Definition |
|------|-----------|
| PFM | Personal Finance Management |
| PSD2 | EU Payment Services Directive 2 — mandates open banking APIs |
| AISP | Account Information Service Provider — licensed entity for bank data access |
| XS2A | Access to Account — Berlin Group standard for PSD2 APIs |
| ARR | Annual Recurring Revenue |
| ARPU | Average Revenue Per User |
| CAC | Customer Acquisition Cost |
| LTV | Lifetime Value |
| DACH | Deutschland, Austria, Switzerland |
| Freemium | Free base product with paid premium features |

---

*This business plan is based on market research conducted in July 2026 and reflects the current state of the Fiscal North product and codebase. Financial projections are illustrative and should be validated with actual conversion data post-launch.*

**Prepared by:** Hinni Siemsen · Fiscal North  
**Contact:** [Insert contact information]
