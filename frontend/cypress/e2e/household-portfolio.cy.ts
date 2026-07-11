describe('Household and portfolio', () => {
  beforeEach(() => {
    cy.login('alex@fiscalnorth.local', 'demo1234');
  });

  it('shows household with Alex and Jamie', () => {
    cy.visit('/household');
    cy.contains('Alex');
    cy.contains('Jamie');
  });

  it('displays shared portfolio with holdings', () => {
    cy.visit('/portfolio');
    cy.contains('AAPL');
    cy.contains('VWCE.DE');
    cy.contains('Total value');
  });

  it('shows budget remaining and member breakdown', () => {
    cy.visit('/budgets');
    cy.contains('remaining');
    cy.contains('Alex');
    cy.contains('Jamie');
  });
});
