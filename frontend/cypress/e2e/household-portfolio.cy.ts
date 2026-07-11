describe('Household, portfolio, splits, and demo paywall', () => {
  beforeEach(() => {
    cy.login('alex@fiscalnorth.local', 'demo1234');
  });

  it('shows household with Alex and Jamie', () => {
    cy.visit('/household');
    cy.contains('Alex');
    cy.contains('Jamie');
  });

  it('displays shared portfolio with holdings and allocation', () => {
    cy.visit('/portfolio');
    cy.contains('AAPL');
    cy.contains('VWCE.DE');
    cy.contains(/total value|gesamtwert/i);
    cy.contains(/asset allocation|asset allocation/i);
  });

  it('shows budget remaining and member breakdown', () => {
    cy.visit('/budgets');
    cy.contains(/remaining|übrig/i);
    cy.contains('Alex');
    cy.contains('Jamie');
  });

  it('shows split transaction in list', () => {
    cy.visit('/transactions');
    cy.contains('Kaufland Wocheneinkauf');
    cy.contains(/split/i);
  });

  it('unlocks premium assistant via try in demo', () => {
    cy.visit('/assistant');
    cy.contains(/try in demo|in demo testen/i).click();
    cy.get('app-paywall-banner').should('not.exist');
  });

  it('loads household join page with token query', () => {
    cy.visit('/household/join?token=demo-token');
    cy.contains(/join household|haushalt beitreten/i);
    cy.contains(/accept invitation|einladung annehmen/i);
  });
});
