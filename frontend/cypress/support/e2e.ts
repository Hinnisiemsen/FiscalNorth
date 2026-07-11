/// <reference types="cypress" />

import './commands';

declare global {
  namespace Cypress {
    interface Chainable {
      login(email?: string, password?: string): Chainable<void>;
      waitForTranslations(): Chainable<void>;
      visitApp(path: string): Chainable<void>;
    }
  }
}

export {};
