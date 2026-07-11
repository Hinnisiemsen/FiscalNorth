/// <reference types="cypress" />

import './commands';

declare global {
  namespace Cypress {
    interface Chainable {
      login(email?: string, password?: string): Chainable<void>;
      visitApp(path: string): Chainable<void>;
      waitForListApi(alias: string, minLength?: number): Chainable<void>;
    }
  }
}

export {};
