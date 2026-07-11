import Foundation

struct PortfolioService {
    private let api = APIClient.shared

    func getPortfolio() async throws -> PortfolioOverview {
        try await api.get("portfolio")
    }
}
