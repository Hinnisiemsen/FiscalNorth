describe('Transactions', () => {
  beforeEach(() => {
    cy.login();
  });

  it('lists transactions and opens create form', () => {
    cy.intercept('GET', '/api/transaction/payment').as('transactions');
    cy.visitApp('/transactions');
    cy.wait('@transactions');
    cy.contains('a', /transaction|transaktion/i).should('exist');
    cy.visitApp('/transactions/new');
    cy.get('#description').should('be.visible');
    cy.get('#amount').should('be.visible');
  });

  it('creates a simple expense transaction', () => {
    cy.intercept('POST', '/api/transaction/payment').as('createTransaction');
    cy.intercept('GET', '/api/transaction/payment').as('transactions');
    cy.visitApp('/transactions/new');
    cy.get('#description').type('E2E Test Coffee');
    cy.get('#amount').clear().type('4.50');
    cy.get('button[type="submit"]').click();
    cy.wait('@createTransaction').its('response.statusCode').should('eq', 201);
    cy.url({ timeout: 15000 }).should('include', '/transactions');
    cy.wait('@transactions');
    cy.contains('E2E Test Coffee', { timeout: 15000 });
  });
});
