/// <reference types="cypress" />

function csrfTokenFromResponse(response: Cypress.Response<unknown>): string {
  const setCookie = response.headers['set-cookie'];
  const cookies = Array.isArray(setCookie) ? setCookie : setCookie ? [setCookie] : [];
  const raw = cookies.find((entry) => entry.startsWith('XSRF-TOKEN='))?.split(';')[0]?.split('=')[1];
  expect(raw, 'XSRF-TOKEN cookie').to.be.a('string');
  return decodeURIComponent(raw!);
}

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session(
    [email, password],
    () => {
      cy.request('GET', '/api/auth/csrf').then((csrfResponse) => {
        const csrfToken = csrfTokenFromResponse(csrfResponse);
        cy.request({
          method: 'POST',
          url: '/api/auth/login',
          body: { email, password },
          headers: { 'X-XSRF-TOKEN': csrfToken },
        }).its('status').should('eq', 200);
      });
    },
    {
      validate() {
        cy.request('/api/auth/status').its('body.authenticated').should('eq', true);
      },
    },
  );
});
