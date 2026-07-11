describe('Financial goals', () => {
  beforeEach(() => {
    cy.login();
  });

  it('shows seeded goals with progress overview', () => {
    cy.visitApp('/goals');
    cy.get('.kpi-strip').should('be.visible');
    cy.contains('.goal-card', 'Notgroschen').should('be.visible');
    cy.contains('.goal-card', 'Sommerurlaub').should('be.visible');
    cy.get('.progress-fill').should('have.length.at.least', 2);
  });

  it('opens goal detail with progress percentage', () => {
    cy.visitApp('/goals');
    cy.contains('.goal-card', 'Notgroschen').click();
    cy.url().should('match', /\/goals\/\d+$/);
    cy.get('.progress-percent').should('be.visible');
    cy.contains('.progress-percent', /%$/);
  });

  it('starts the goal interview wizard and reaches AI plan step', () => {
    cy.visitApp('/goals/new');
    cy.get('.wizard', { timeout: 15000 }).should('be.visible');
    cy.get('.priority-chip').first().click();
    cy.get('.wizard-actions .btn-primary').should('not.be.disabled').click();
    cy.get('.target-row input[type="number"]', { timeout: 10000 }).first().type('4000').blur();
    cy.get('.wizard-actions .btn-primary').should('not.be.disabled').click();
    cy.get('input[name="monthly"]').should('be.visible').clear().type('250').blur();
    cy.get('.wizard-actions .btn-primary').should('not.be.disabled').click();
    cy.get('.wizard-actions .btn-primary').click();
    // Demo preview grants access: plan generates (or paywall shows when billing-only mode).
    cy.get('app-paywall-banner, .plan-summary, .plan-loading', { timeout: 20000 }).should('exist');
  });
});
