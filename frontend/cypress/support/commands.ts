/// <reference types="cypress" />

function csrfHeaderFromCookie(): Cypress.Chainable<{ 'X-XSRF-TOKEN': string }> {
  return cy.getCookie('XSRF-TOKEN').then((cookie) => {
    expect(cookie?.value, 'CSRF cookie').to.be.a('string');
    return { 'X-XSRF-TOKEN': decodeURIComponent(cookie!.value) };
  });
}

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session([email, password], () => {
    cy.request('/api/auth/status');
    csrfHeaderFromCookie().then((headers) => {
      cy.request({
        method: 'POST',
        url: '/api/auth/login',
        body: { email, password },
        headers,
      }).its('status').should('eq', 200);
    });
  });
});
