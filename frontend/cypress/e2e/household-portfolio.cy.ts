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
    cy.wait('@portfolio').its('response.statusCode').should('eq', 200);
    cy.get('.summary-grid').should('be.visible');
    cy.get('.holding-card').contains('AAPL');
    cy.get('.holding-card').contains('VWCE.DE');
    cy.get('.allocation-card').should('be.visible');
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
    cy.waitForListApi('transactions', 1);
    cy.contains('.list-card-title', 'Kaufland Wocheneinkauf', { timeout: 15000 });
    cy.contains(/split/i);
  });

  it('unlocks premium assistant via try in demo', () => {
    cy.intercept('GET', '/api/user/me').as('profile');
    cy.visitApp('/assistant');
    cy.wait('@profile').its('response.body.subscription.premiumPreviewEnabled').should('eq', true);
    cy.get('app-paywall-banner', { timeout: 15000 }).should('be.visible');
    cy.get('app-paywall-banner .btn-secondary.paywall-cta').click();
    cy.get('app-paywall-banner').should('not.exist');
  });

  it('loads household join page with token query', () => {
    cy.visitApp('/household/join?token=demo-token');
    cy.get('.join-card').should('be.visible');
    cy.get('.join-card button.btn-primary').should('be.visible');
  });
});
