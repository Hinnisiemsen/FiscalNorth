/// <reference types="cypress" />

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session(
    [email, password],
    () => {
      cy.visit('/login');
      cy.window().then((win) =>
        win.fetch('/api/auth/csrf', { credentials: 'include' }).then((response) => {
          expect(response.status).to.eq(204);
        }),
      );
      cy.get('#login-email', { timeout: 15000 }).should('be.visible').clear().type(email);
      cy.get('#login-password').clear().type(password, { log: false });
      cy.get('form.auth-form button[type="submit"]').click();
      cy.url({ timeout: 15000 }).should('not.include', '/login');
    },
    {
      validate() {
        cy.request('/api/auth/status').its('body.authenticated').should('eq', true);
      },
    },
  );
});
