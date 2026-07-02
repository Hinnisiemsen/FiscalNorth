export type DashboardPanel = 'fazit' | 'monat' | 'kategorien' | 'verlauf' | 'budgets' | 'goals' | 'hinweise';

export type AskContext = DashboardPanel | 'general' | 'analysis';

export interface AskContextConfig {
  placeholder: string;
  suggestions: string[];
  buildQuery: (periodLabel: string) => string;
  buildAnalysisQuery: (periodLabel: string) => string;
}

const panelAnalysis = (topic: string, period: string) =>
  `Analysiere für ${period} nur den Bereich „${topic}“. ` +
  `Antworte auf Deutsch in 3–5 kurzen Sätzen als Fließtext (kein JSON, keine Aufzählungsaktionen). ` +
  `Nenne konkrete Zahlen aus den Daten und ein klares Fazit.`;

export const ASK_CONTEXTS: Record<AskContext, AskContextConfig> = {
  general: {
    placeholder: 'z. B. Wie steht es um meine Finanzen diesen Monat?',
    suggestions: ['Wo kann ich am meisten sparen?', 'Wie ist mein Monatssaldo?'],
    buildQuery: (period) => `Gib mir eine kurze Einschätzung meiner Finanzlage in ${period}.`,
    buildAnalysisQuery: (period) => panelAnalysis('Gesamtübersicht', period),
  },
  analysis: {
    placeholder: 'Gesamtanalyse — Fiscal North wertet alle deine Daten aus',
    suggestions: [],
    buildQuery: (period) =>
      `Erstelle eine Gesamtanalyse meiner Finanzen für ${period}: Stärken, Risiken und 3 konkrete Empfehlungen.`,
    buildAnalysisQuery: (period) => panelAnalysis('Gesamtanalyse', period),
  },
  fazit: {
    placeholder: 'Frage zum Fazit…',
    suggestions: ['Was bedeutet das für mich?', 'Was soll ich als Nächstes tun?'],
    buildQuery: (period) =>
      `Erkläre das Fazit zu meiner Finanzlage in ${period} und was ich konkret tun sollte.`,
    buildAnalysisQuery: (period) => panelAnalysis('Fazit', period),
  },
  monat: {
    placeholder: 'Frage zu Einnahmen, Ausgaben, Saldo…',
    suggestions: ['Warum ist mein Saldo so?', 'Vergleiche mit dem Vormonat'],
    buildQuery: (period) =>
      `Analysiere meinen Monat ${period}: Einnahmen, Ausgaben und Saldo im Vergleich zum Vormonat.`,
    buildAnalysisQuery: (period) => panelAnalysis('Monat im Detail', period),
  },
  kategorien: {
    placeholder: 'Frage zu Ausgaben-Kategorien…',
    suggestions: ['Wo gebe ich zu viel aus?', 'Welche Kategorie sparen?'],
    buildQuery: (period) =>
      `Analysiere meine Ausgaben nach Kategorien in ${period} und nenne den größten Hebel.`,
    buildAnalysisQuery: (period) => panelAnalysis('Ausgaben nach Kategorie', period),
  },
  verlauf: {
    placeholder: 'Frage zum Verlauf…',
    suggestions: ['Entwickelt sich mein Saldo positiv?'],
    buildQuery: (period) =>
      `Bewerte den Verlauf meiner Finanzen über die letzten Monate bis ${period}.`,
    buildAnalysisQuery: (period) => panelAnalysis('Verlauf', period),
  },
  budgets: {
    placeholder: 'Frage zu Budgets…',
    suggestions: ['Welches Budget ist kritisch?', 'Schlage ein neues Budget vor'],
    buildQuery: (period) =>
      `Prüfe meine aktiven Budgets in ${period} und sage mir, wo Handlungsbedarf ist.`,
    buildAnalysisQuery: (period) => panelAnalysis('Budgets', period),
  },
  goals: {
    placeholder: 'Frage zu Finanzzielen…',
    suggestions: ['Bin ich im Plan?', 'Wie viel soll ich monatlich sparen?'],
    buildQuery: () => `Wie stehe ich bei meinen Finanzzielen und was soll ich als Nächstes tun?`,
    buildAnalysisQuery: (period) => panelAnalysis('Finanzziele', period),
  },
  hinweise: {
    placeholder: 'Frage zu Hinweisen…',
    suggestions: ['Was ist am dringendsten?'],
    buildQuery: () => `Erkläre meine aktuellen Hinweise und priorisiere sie.`,
    buildAnalysisQuery: (_period) =>
      `Erkläre meine aktuellen Hinweise, priorisiere sie und sage, was ich zuerst tun sollte.`,
  },
};
