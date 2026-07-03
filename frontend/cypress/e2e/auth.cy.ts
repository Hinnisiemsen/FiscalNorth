describe('Authentication smoke', () => {
  it('redirects unauthenticated users to login', () => {
    cy.visit('/goals');
    cy.url({ timeout: 15000 }).should('include', '/login');
    cy.get('#login-email').should('be.visible');
  });

  it('logs in with demo credentials and reaches the dashboard', () => {
    cy.login();
    cy.visit('/');
    cy.contains('h1', /.+/);
    cy.get('.overview-strip').should('exist');
  });
});
