/// <reference types="cypress" />

function readBrowserCsrfToken(win: Window): string {
  const match = win.document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]*)/);
  expect(match?.[1], 'browser CSRF cookie').to.be.a('string');
  return decodeURIComponent(match![1]);
}

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session(
    [email, password],
    () => {
      cy.visit('/login');
      cy.window().then(async (win) => {
        const csrfResponse = await win.fetch('/api/auth/csrf', { credentials: 'include' });
        expect(csrfResponse.status).to.eq(204);

        const csrfToken = readBrowserCsrfToken(win);
        const loginResponse = await win.fetch('/api/auth/login', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrfToken,
          },
          body: JSON.stringify({ email, password }),
        });
        expect(loginResponse.status).to.eq(200);
      });
    },
    {
      validate() {
        cy.visit('/');
        cy.url({ timeout: 10000 }).should('not.include', '/login');
      },
    },
  );
});
