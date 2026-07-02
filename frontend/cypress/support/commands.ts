/// <reference types="cypress" />

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session([email, password], () => {
    cy.visit('/login');
    cy.get('#login-email').clear().type(email);
    cy.get('#login-password').clear().type(password, { log: false });
    cy.get('form.auth-form button[type="submit"]').click();
    cy.url({ timeout: 10000 }).should('not.include', '/login');
  });
});
