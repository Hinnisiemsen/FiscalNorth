# Fiscal North — Business Documents

This folder contains investor and strategy materials for Fiscal North.

## Documents

| File | Description | Format |
|------|-------------|--------|
| [BUSINESS_PLAN.md](./BUSINESS_PLAN.md) | Full business plan (16 sections) | Markdown |
| [MARKET_ANALYSIS_SLIDEDECK.md](./MARKET_ANALYSIS_SLIDEDECK.md) | Complete market analysis presentation (~35 slides) | Marp slides |
| [PITCH_DECK.md](./PITCH_DECK.md) | Investor pitch deck (~18 slides) | Marp slides |
| [market-analysis.pdf](./market-analysis.pdf) | Exported market analysis deck | PDF |
| [pitch-deck.pdf](./pitch-deck.pdf) | Exported investor pitch | PDF |
| [pitch-deck.pptx](./pitch-deck.pptx) | Exported investor pitch | PowerPoint |

## Rendering Slide Decks

Both slide decks use [Marp](https://marp.app/) markdown format. Export to PDF or PowerPoint:

```bash
npm install -g @marp-team/marp-cli

marp docs/business/MARKET_ANALYSIS_SLIDEDECK.md --pdf -o market-analysis.pdf
marp docs/business/PITCH_DECK.md --pdf -o pitch-deck.pdf
marp docs/business/PITCH_DECK.md --pptx -o pitch-deck.pptx
```

In VS Code / Cursor, install the **Marp for VS Code** extension for live slide preview.

## Key Themes

- **Product:** AI-native personal finance platform for Europe (live MVP)
- **Wedge:** Shared household portfolio (planned Q4 2026)
- **Market:** ~$3.1B EU PFM, ~13% CAGR; ~$1.34B AI-in-finance, ~22% CAGR
- **Model:** Freemium SaaS — Free / Premium (€4.99) / Household (€7.99) / Household Plus (€11.99)
- **Ask:** €500K–750K seed for mobile, household features, and DACH GTM
