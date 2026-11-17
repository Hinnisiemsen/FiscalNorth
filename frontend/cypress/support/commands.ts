/// <reference types="cypress" />

const seedClientPrefs = (win: Window) => {
  win.localStorage.setItem('fn-season', 'default');
  win.localStorage.setItem('fn.locale', 'en');
};

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session(
    [email, password],
    () => {
      cy.visit('/login', { onBeforeLoad: seedClientPrefs });
      cy.window().then(async (win) => {
        const csrfResponse = await win.fetch('/api/auth/csrf', { credentials: 'include' });
        expect(csrfResponse.status).to.eq(200);
        const csrf = (await csrfResponse.json()) as { token: string; headerName: string };

        const loginResponse = await win.fetch('/api/auth/login', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            [csrf.headerName]: csrf.token,
          },
          body: JSON.stringify({ email, password }),
        });
        expect(loginResponse.status).to.eq(200);
      });
    },
    {
      validate() {
        cy.visit('/', { onBeforeLoad: seedClientPrefs });
        cy.url({ timeout: 10000 }).should('not.include', '/login');
      },
    },
  );
});
