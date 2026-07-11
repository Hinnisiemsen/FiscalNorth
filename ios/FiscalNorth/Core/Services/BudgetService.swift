import Foundation

struct BudgetService {
    private let api = APIClient.shared

    func getBudgetsWithUsage() async throws -> [BudgetWithUsage] {
        try await api.get("budget/with-usage")
    }
}
