import SwiftUI

struct DashboardView: View {
    @EnvironmentObject private var authService: AuthService

    @State private var summary: DashboardSummary?
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let dashboardService = DashboardService()

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    ProgressView("Loading dashboard…")
                } else if let errorMessage {
                    ContentUnavailableView("Unable to load dashboard", systemImage: "exclamationmark.triangle", description: Text(errorMessage))
                } else if let summary {
                    ScrollView {
                        VStack(spacing: 16) {
                            greetingCard(summary)
                            kpiGrid(summary)
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("Dashboard")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Sign out") {
                        Task { await authService.logout() }
                    }
                }
            }
            .refreshable {
                await loadDashboard()
            }
            .task {
                await loadDashboard()
            }
        }
    }

    @ViewBuilder
    private func greetingCard(_ summary: DashboardSummary) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Hello, \(summary.userName)")
                .font(.title2.bold())
            Text("Household overview")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(.thinMaterial, in: RoundedRectangle(cornerRadius: 16))
    }

    @ViewBuilder
    private func kpiGrid(_ summary: DashboardSummary) -> some View {
        LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
            KPICard(title: "Net worth", value: CurrencyFormatter.format(summary.netWorth), icon: "banknote.fill")
            KPICard(title: "Cash balance", value: CurrencyFormatter.format(summary.totalBalance), icon: "eurosign.circle.fill")
            KPICard(title: "Portfolio", value: CurrencyFormatter.format(summary.portfolioValue), icon: "chart.line.uptrend.xyaxis")
            KPICard(
                title: "Budget spent",
                value: budgetLabel(spent: summary.householdBudgetSpent, limit: summary.householdBudgetLimit),
                icon: "chart.pie.fill"
            )
        }
    }

    private func budgetLabel(spent: Double, limit: Double) -> String {
        guard limit > 0 else {
            return CurrencyFormatter.format(spent)
        }
        return "\(CurrencyFormatter.format(spent)) / \(CurrencyFormatter.format(limit))"
    }

    private func loadDashboard() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        guard let userName = authService.currentUser?.userName else {
            errorMessage = "Missing user profile."
            return
        }

        do {
            summary = try await dashboardService.loadSummary(userName: userName)
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

private struct KPICard: View {
    let title: String
    let value: String
    let icon: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Label(title, systemImage: icon)
                .font(.caption)
                .foregroundStyle(.secondary)
            Text(value)
                .font(.headline)
                .minimumScaleFactor(0.8)
                .lineLimit(2)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(.background, in: RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .strokeBorder(.quaternary)
        )
    }
}

#Preview {
    DashboardView()
        .environmentObject(AuthService())
}
