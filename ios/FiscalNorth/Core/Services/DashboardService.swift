import Foundation

struct DashboardService {
    private let accountService = AccountService()
    private let budgetService = BudgetService()
    private let portfolioService = PortfolioService()

    func loadSummary(userName: String) async throws -> DashboardSummary {
        async let totalBalance = accountService.totalBalance()
        async let budgets = budgetService.getBudgetsWithUsage()
        async let portfolio = portfolioService.getPortfolio()

        let (balance, budgetList, portfolioData) = try await (totalBalance, budgets, portfolio)

        let budgetSpent = budgetList.reduce(0) { $0 + $1.spent }
        let budgetLimit = budgetList.reduce(0) { $0 + $1.limit }

        return DashboardSummary(
            userName: userName,
            totalBalance: balance,
            portfolioValue: portfolioData.totalValue,
            netWorth: balance + portfolioData.totalValue,
            householdBudgetSpent: budgetSpent,
            householdBudgetLimit: budgetLimit
        )
    }
}
