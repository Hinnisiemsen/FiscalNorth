/// <reference types="cypress" />

Cypress.Commands.add('login', (email = 'alex@fiscalnorth.local', password = 'demo1234') => {
  cy.session(
    [email, password],
    () => {
      cy.visit('/login', {
        onBeforeLoad(win) {
          win.localStorage.setItem('fn.locale', 'en');
        },
      });
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
        cy.visit('/', {
          onBeforeLoad(win) {
            win.localStorage.setItem('fn.locale', 'en');
          },
        });
        cy.url({ timeout: 10000 }).should('not.include', '/login');
      },
    },
  );
});

/** Visit a route after login with English locale. Does not wait on i18n fetches (cached after first load). */
Cypress.Commands.add('visitApp', (path: string) => {
  cy.visit(path, {
    onBeforeLoad(win) {
      win.localStorage.setItem('fn.locale', 'en');
      win.sessionStorage.removeItem('fiscalnorth.demoPaywallDismissed');
    },
  });
  cy.get('.page-content-inner, .login-card, .wizard', { timeout: 15000 }).should('exist');
});

/** Wait for a successful API response with a JSON array body. */
Cypress.Commands.add('waitForListApi', (alias: string, minLength = 1) => {
  cy.wait(`@${alias}`, { timeout: 20000 }).then(({ response }) => {
    expect(response?.statusCode).to.eq(200);
    expect(response?.body).to.be.an('array').and.have.length.at.least(minLength);
  });
});
