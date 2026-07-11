import Foundation

struct CsrfTokenResponse: Decodable {
    let token: String
    let headerName: String
}

struct AuthStatus: Decodable {
    let authenticated: Bool
    let provider: String?
}

struct LoginRequest: Encodable {
    let email: String
    let password: String
}

struct RegisterRequest: Encodable {
    let userName: String
    let email: String
    let password: String
}

struct UserProfile: Decodable, Identifiable {
    let id: Int?
    let userName: String
    let email: String?
    let avatarUrl: String?
    let authProvider: String
    let locale: String
    let subscription: SubscriptionSummary
}

struct SubscriptionSummary: Decodable {
    let plan: String
    let status: String
    let entitlements: [String]
    let currentPeriodEnd: String?
    let trialEnd: String?
    let cancelAtPeriodEnd: Bool
    let premiumActive: Bool
    let billingEnabled: Bool?
    let premiumPreviewEnabled: Bool?
    let paidSubscriptionActive: Bool?
}

struct DepositAccount: Decodable, Identifiable {
    let id: Int
    let name: String
    let currency: String
    let balance: Double
    let interestRate: Double
    let term: String
    let renewable: Bool
}

struct BankAccount: Decodable, Identifiable {
    let id: Int
    let name: String
    let currency: String
    let balance: Double
    let bankName: String
    let iban: String
    let bic: String
    let accountType: String
}

struct CryptoAccount: Decodable, Identifiable {
    let id: Int
    let name: String
    let currency: String
    let balance: Double
    let walletAddress: String
    let provider: String?
}

enum AccountKind: String {
    case deposit = "DEPOSIT"
    case bank = "BANK"
    case crypto = "CRYPTO"
}

struct UnifiedAccount: Identifiable {
    let id: Int
    let kind: AccountKind
    let name: String
    let currency: String
    let balance: Double
    let subtitle: String?
}

struct CategoryRef: Decodable {
    let id: Int
    let name: String
}

struct TransactionSplit: Decodable, Identifiable {
    let id: Int
    let amount: Double
    let note: String?
    let category: CategoryRef?
}

struct PaymentTransaction: Decodable, Identifiable {
    let id: Int
    let amount: Double
    let description: String
    let transactionDate: String
    let transactionType: String
    let category: CategoryRef?
    let splits: [TransactionSplit]?
}

struct MemberSpending: Decodable {
    let memberName: String
    let spent: Double
}

struct BudgetWithUsage: Decodable, Identifiable {
    let id: Int
    let name: String
    let limit: Double
    let startDate: String
    let endDate: String
    let spent: Double
    let remaining: Double
    let categoryId: Int?
    let categoryName: String?
    let memberBreakdown: [MemberSpending]?
}

struct HoldingView: Decodable, Identifiable {
    let id: Int
    let symbol: String
    let name: String
    let quantity: Double
    let costBasis: Double
    let assetClass: String
    let currentPrice: Double
    let marketValue: Double
    let unrealizedGain: Double
    let lastUpdatedBy: String?
    let priceStale: Bool
}

struct PortfolioOverview: Decodable {
    let id: Int
    let name: String
    let totalValue: Double
    let totalCost: Double
    let unrealizedGain: Double
    let holdings: [HoldingView]
}

struct DashboardSummary {
    let userName: String
    let totalBalance: Double
    let portfolioValue: Double
    let netWorth: Double
    let householdBudgetSpent: Double
    let householdBudgetLimit: Double
}
