import SwiftUI

struct BudgetsView: View {
    @State private var budgets: [BudgetWithUsage] = []
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let budgetService = BudgetService()

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    ProgressView("Loading budgets…")
                } else if let errorMessage {
                    ContentUnavailableView("Unable to load budgets", systemImage: "exclamationmark.triangle", description: Text(errorMessage))
                } else if budgets.isEmpty {
                    ContentUnavailableView("No budgets", systemImage: "chart.pie", description: Text("Create budgets in the web app."))
                } else {
                    List(budgets) { budget in
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(budget.name)
                                    .font(.headline)
                                Spacer()
                                Text(CurrencyFormatter.format(budget.remaining))
                                    .font(.subheadline.bold())
                                    .foregroundStyle(budget.remaining >= 0 ? .green : .red)
                            }

                            ProgressView(value: min(budget.spent / max(budget.limit, 1), 1))
                                .tint(budget.remaining >= 0 ? .accentColor : .red)

                            HStack {
                                Text("\(CurrencyFormatter.format(budget.spent)) spent")
                                Spacer()
                                Text("Limit \(CurrencyFormatter.format(budget.limit))")
                            }
                            .font(.caption)
                            .foregroundStyle(.secondary)

                            if let categoryName = budget.categoryName {
                                Text(categoryName)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }

                            if let breakdown = budget.memberBreakdown, !breakdown.isEmpty {
                                ForEach(breakdown, id: \.memberName) { member in
                                    HStack {
                                        Text(member.memberName)
                                        Spacer()
                                        Text(CurrencyFormatter.format(member.spent))
                                    }
                                    .font(.caption2)
                                    .foregroundStyle(.secondary)
                                }
                            }
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
            .navigationTitle("Budgets")
            .refreshable {
                await loadBudgets()
            }
            .task {
                await loadBudgets()
            }
        }
    }

    private func loadBudgets() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            budgets = try await budgetService.getBudgetsWithUsage()
                .sorted { $0.name.localizedCaseInsensitiveCompare($1.name) == .orderedAscending }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

#Preview {
    BudgetsView()
}
