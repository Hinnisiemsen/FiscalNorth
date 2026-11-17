describe('Transactions', () => {
  beforeEach(() => {
    cy.login();
  });

  it('lists transactions and opens create form', () => {
    cy.visit('/transactions');
    cy.contains('a', /transaction|transaktion/i).should('exist');
    cy.visit('/transactions/new');
    cy.get('#description').should('be.visible');
    cy.get('#amount').should('be.visible');
  });

  it('creates a simple expense transaction', () => {
    cy.visit('/transactions/new');
    cy.get('#description').type('E2E Test Coffee');
    cy.get('#amount').clear().type('4.50');
    cy.get('button[type="submit"]').click();
    cy.url({ timeout: 15000 }).should('match', /\/transactions\/?$/);
    cy.contains('.list-card-title', 'E2E Test Coffee').scrollIntoView().should('be.visible');
  });
});
