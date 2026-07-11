import SwiftUI

struct MainTabView: View {
    @EnvironmentObject private var authService: AuthService

    var body: some View {
        TabView {
            DashboardView()
                .tabItem {
                    Label("Home", systemImage: "house.fill")
                }

            AccountsView()
                .tabItem {
                    Label("Accounts", systemImage: "creditcard.fill")
                }

            TransactionsView()
                .tabItem {
                    Label("Transactions", systemImage: "list.bullet.rectangle")
                }

            BudgetsView()
                .tabItem {
                    Label("Budgets", systemImage: "chart.pie.fill")
                }

            PortfolioView()
                .tabItem {
                    Label("Portfolio", systemImage: "chart.line.uptrend.xyaxis")
                }
        }
    }
}

#Preview {
    MainTabView()
        .environmentObject(AuthService())
}
