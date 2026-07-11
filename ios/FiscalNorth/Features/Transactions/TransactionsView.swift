import SwiftUI

struct TransactionsView: View {
    @State private var transactions: [PaymentTransaction] = []
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let transactionService = TransactionService()

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    ProgressView("Loading transactions…")
                } else if let errorMessage {
                    ContentUnavailableView("Unable to load transactions", systemImage: "exclamationmark.triangle", description: Text(errorMessage))
                } else if transactions.isEmpty {
                    ContentUnavailableView("No transactions", systemImage: "list.bullet.rectangle", description: Text("Transactions will appear here once recorded."))
                } else {
                    List(transactions) { transaction in
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text(transaction.description)
                                    .font(.headline)
                                Spacer()
                                Text(CurrencyFormatter.format(transaction.amount))
                                    .font(.headline)
                                    .foregroundStyle(transaction.amount < 0 ? .primary : .green)
                            }
                            HStack {
                                Text(DateFormatterHelper.displayDate(transaction.transactionDate))
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                                if let category = transaction.category?.name {
                                    Text("•")
                                        .foregroundStyle(.secondary)
                                    Text(category)
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                                if let splits = transaction.splits, !splits.isEmpty {
                                    Text("•")
                                        .foregroundStyle(.secondary)
                                    Text("\(splits.count) splits")
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                }
                            }
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
            .navigationTitle("Transactions")
            .refreshable {
                await loadTransactions()
            }
            .task {
                await loadTransactions()
            }
        }
    }

    private func loadTransactions() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            transactions = try await transactionService.getPaymentTransactions()
                .sorted { $0.transactionDate > $1.transactionDate }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

#Preview {
    TransactionsView()
}
