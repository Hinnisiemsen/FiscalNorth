import Foundation

struct TransactionService {
    private let api = APIClient.shared

    func getPaymentTransactions() async throws -> [PaymentTransaction] {
        try await api.get("transaction/payment")
    }
}
