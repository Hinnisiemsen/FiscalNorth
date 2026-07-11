describe('Household, portfolio, splits, and demo paywall', () => {
  beforeEach(() => {
    cy.login('alex@fiscalnorth.local', 'demo1234');
  });

  it('shows household with Alex and Jamie', () => {
    cy.visitApp('/household');
    cy.contains('Alex');
    cy.contains('Jamie');
  });

  it('displays shared portfolio with holdings and allocation', () => {
    cy.intercept('GET', '/api/portfolio').as('portfolio');
    cy.visitApp('/portfolio');
    cy.wait('@portfolio');
    cy.contains('AAPL');
    cy.contains('VWCE.DE');
    cy.contains(/total value|gesamtwert/i);
    cy.contains(/asset allocation|asset-allokation/i);
  });

  it('shows budget remaining and member breakdown', () => {
    cy.visitApp('/budgets');
    cy.contains(/remaining|übrig/i);
    cy.contains('Alex');
    cy.contains('Jamie');
  });

  it('shows split transaction in list', () => {
    cy.intercept('GET', '/api/transaction/payment').as('transactions');
    cy.visitApp('/transactions');
    cy.wait('@transactions');
    cy.contains('Kaufland Wocheneinkauf', { timeout: 15000 });
    cy.contains(/split/i);
  });

  it('unlocks premium assistant via try in demo', () => {
    cy.visitApp('/assistant');
    cy.contains(/try in demo|in demo testen/i, { timeout: 15000 }).click();
    cy.get('app-paywall-banner').should('not.exist');
  });

  it('loads household join page with token query', () => {
    cy.visitApp('/household/join?token=demo-token');
    cy.contains(/join household|haushalt beitreten/i);
    cy.contains(/accept invitation|einladung annehmen/i);
  });
});
