export type SubscriptionPlan = 'FREE' | 'PREMIUM';

export type SubscriptionStatus =
  | 'ACTIVE'
  | 'TRIALING'
  | 'PAST_DUE'
  | 'CANCELED'
  | 'UNPAID'
  | 'INCOMPLETE'
  | 'INCOMPLETE_EXPIRED'
  | 'NONE';

export type PremiumFeature = 'AI_ASSISTANT' | 'AI_GOAL_PLANNER' | 'BANK_SYNC' | 'AI_NOTIFICATIONS';

export interface SubscriptionSummary {
  plan: SubscriptionPlan;
  status: SubscriptionStatus;
  entitlements: PremiumFeature[];
  currentPeriodEnd: string | null;
  trialEnd: string | null;
  cancelAtPeriodEnd: boolean;
  premiumActive: boolean;
}

export interface SubscriptionStatusResponse extends SubscriptionSummary {
  billingEnabled: boolean;
}

export interface BillingPlan {
  id: string;
  name: string;
  priceId: string;
  interval: 'month' | 'year';
  trialDays: number;
}

export interface BillingPlansResponse {
  plans: BillingPlan[];
  billingEnabled: boolean;
}

export interface SessionUrlResponse {
  url: string;
}
