import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { EntitlementService } from './entitlement.service';
import { BillingService } from './billing.service';
import { UserProfile } from './user.service';

describe('EntitlementService', () => {
  let service: EntitlementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EntitlementService,
        {
          provide: BillingService,
          useValue: {
            getSubscription: () =>
              of({
                plan: 'PREMIUM',
                status: 'ACTIVE',
                entitlements: ['AI_ASSISTANT', 'BANK_SYNC'],
                currentPeriodEnd: null,
                trialEnd: null,
                cancelAtPeriodEnd: false,
                premiumActive: true,
                billingEnabled: true,
              }),
          },
        },
      ],
    });
    service = TestBed.inject(EntitlementService);
  });

  it('syncs entitlements from user profile', () => {
    const profile = {
      id: 1,
      userName: 'Alex',
      email: 'alex@example.com',
      avatarUrl: null,
      authProvider: 'LOCAL',
      locale: 'en',
      subscription: {
        plan: 'FREE',
        status: 'NONE',
        entitlements: [],
        currentPeriodEnd: null,
        trialEnd: null,
        cancelAtPeriodEnd: false,
        premiumActive: false,
      },
    } as UserProfile;

    service.syncFromProfile(profile);
    expect(service.hasFeature('AI_ASSISTANT')).toBeFalse();
  });

  it('detects premium required API errors', () => {
    expect(service.isPremiumRequiredError({ status: 403 })).toBeTrue();
    expect(service.isPremiumRequiredError({ status: 401 })).toBeFalse();
  });
});
