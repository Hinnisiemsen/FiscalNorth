describe('Bank sync', () => {
  beforeEach(() => {
    cy.login();
  });

  it('loads bank sync page', () => {
    cy.visit('/bank-sync');
    cy.contains(/bank|sync|verbinden/i);
  });
});
