/// <reference types="cypress" />

function readBrowserCsrfToken(win: Window): string | null {
  const match = win.document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]*)/);
  return match?.[1] ? decodeURIComponent(match[1]) : null;
}

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session(
    [email, password],
    () => {
      cy.visit('/login');
      cy.window().then(async (win) => {
        const csrfResponse = await win.fetch('/api/auth/csrf', { credentials: 'include' });
        expect(csrfResponse.status).to.eq(204);

        const csrfToken =
          csrfResponse.headers.get('X-XSRF-TOKEN') ?? readBrowserCsrfToken(win);
        expect(csrfToken, 'CSRF token').to.be.a('string');

        const loginResponse = await win.fetch('/api/auth/login', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrfToken!,
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
