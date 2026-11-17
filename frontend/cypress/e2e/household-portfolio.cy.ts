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
    cy.contains(/asset allocation|asset-allokation/i);
  });

  it('adds an ETF holding from the portfolio form', () => {
    cy.visit('/portfolio');
    cy.get('[data-cy=add-holding]').click();
    cy.get('[data-cy=holding-symbol]').type('IWDA');
    cy.get('[data-cy=holding-name]').type('MSCI World');
    cy.get('[data-cy=holding-quantity]').type('3');
    cy.get('[data-cy=holding-cost]').type('250');
    cy.get('[data-cy=holding-asset-class]').select('ETF');
    cy.get('[data-cy=save-holding]').click();
    cy.contains('IWDA');
    cy.contains(/MSCI World/);
  });

  it('shows budget remaining and member breakdown', () => {
    cy.visit('/budgets');
    cy.contains(/remaining|übrig/i);
    cy.contains('Alex');
    cy.contains('Jamie');
  });

  it('shows split transaction in list', () => {
    cy.visit('/transactions');
    cy.contains('.list-card-title', 'Kaufland Wocheneinkauf').scrollIntoView().should('be.visible');
    cy.contains(/split/i);
  });

  it('unlocks premium assistant via try in demo', () => {
    cy.visit('/assistant');
    cy.get('[data-cy=try-demo]', { timeout: 15000 }).should('be.visible').click();
    cy.get('[data-cy=paywall-banner]').should('not.exist');
  });

  it('loads household join page with token query', () => {
    cy.visit('/household/join?token=demo-token');
    cy.contains(/join household|haushalt beitreten/i);
    cy.contains(/accept invitation|einladung annehmen/i);
  });
});
