import SwiftUI

struct AccountsView: View {
    @State private var accounts: [UnifiedAccount] = []
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let accountService = AccountService()

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    ProgressView("Loading accounts…")
                } else if let errorMessage {
                    ContentUnavailableView("Unable to load accounts", systemImage: "exclamationmark.triangle", description: Text(errorMessage))
                } else if accounts.isEmpty {
                    ContentUnavailableView("No accounts", systemImage: "creditcard", description: Text("Create accounts in the web app."))
                } else {
                    List(accounts) { account in
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text(account.name)
                                    .font(.headline)
                                Spacer()
                                Text(CurrencyFormatter.format(account.balance, currencyCode: account.currency))
                                    .font(.headline)
                            }
                            HStack {
                                Text(account.kind.rawValue)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                                if let subtitle = account.subtitle, !subtitle.isEmpty {
                                    Text("•")
                                        .foregroundStyle(.secondary)
                                    Text(subtitle)
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                        .lineLimit(1)
                                }
                            }
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
            .navigationTitle("Accounts")
            .refreshable {
                await loadAccounts()
            }
            .task {
                await loadAccounts()
            }
        }
    }

    private func loadAccounts() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            accounts = try await accountService.getAllAccounts()
                .sorted { $0.name.localizedCaseInsensitiveCompare($1.name) == .orderedAscending }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

#Preview {
    AccountsView()
}
