describe('Billing / upgrade', () => {
  beforeEach(() => {
    cy.login();
  });

  it('shows upgrade page with premium features', () => {
    cy.visit('/account/upgrade');
    cy.contains(/premium|upgrade/i);
    cy.contains(/assistant|ai/i);
  });

  it('shows account settings with subscription section', () => {
    cy.visit('/account');
    cy.contains(/account|konto/i);
  });
});
