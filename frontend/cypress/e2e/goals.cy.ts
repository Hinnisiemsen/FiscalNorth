describe('Financial goals', () => {
  beforeEach(() => {
    cy.login();
  });

  it('shows seeded goals with progress overview', () => {
    cy.visit('/goals');
    cy.get('.kpi-strip').should('be.visible');
    cy.contains('.goal-card', 'Notgroschen').should('be.visible');
    cy.contains('.goal-card', 'Sommerurlaub').should('be.visible');
    cy.get('.progress-fill').should('have.length.at.least', 2);
  });

  it('opens goal detail with progress percentage', () => {
    cy.visit('/goals');
    cy.contains('.goal-card', 'Notgroschen').click();
    cy.url().should('match', /\/goals\/\d+$/);
    cy.get('.progress-percent').should('be.visible');
    cy.contains('.progress-percent', /%$/);
  });

  it('starts the goal interview wizard', () => {
    cy.visit('/goals/new');
    cy.contains('h2', /.+/);
    cy.get('.priority-chip').first().click();
    cy.contains('button', /Next|Weiter|Suivant|Siguiente/)
      .should('not.be.disabled')
      .click();
    cy.get('.target-row input[type="number"]').first().type('4000');
    cy.contains('button', /Next|Weiter|Suivant|Siguiente/).click();
    cy.get('input[name="monthly"]').clear().type('250');
    cy.contains('button', /Next|Weiter|Suivant|Siguiente/).click();
    cy.contains('button', /Generate|Plan|generieren|générer|Generar/).click();
    cy.contains('.plan-summary', /.+/, { timeout: 15000 });
    cy.get('.recommended-card').should('have.length.at.least', 1);
  });
});
