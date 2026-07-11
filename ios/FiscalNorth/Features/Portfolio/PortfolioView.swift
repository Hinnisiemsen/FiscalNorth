import SwiftUI

struct PortfolioView: View {
    @State private var portfolio: PortfolioOverview?
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let portfolioService = PortfolioService()

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    ProgressView("Loading portfolio…")
                } else if let errorMessage {
                    ContentUnavailableView("Unable to load portfolio", systemImage: "exclamationmark.triangle", description: Text(errorMessage))
                } else if let portfolio {
                    List {
                        Section {
                            summaryRow(title: "Total value", value: CurrencyFormatter.format(portfolio.totalValue))
                            summaryRow(title: "Cost basis", value: CurrencyFormatter.format(portfolio.totalCost))
                            summaryRow(
                                title: "Unrealized gain",
                                value: CurrencyFormatter.format(portfolio.unrealizedGain),
                                valueColor: portfolio.unrealizedGain >= 0 ? .green : .red
                            )
                        }

                        Section("Holdings") {
                            if portfolio.holdings.isEmpty {
                                Text("No holdings yet.")
                                    .foregroundStyle(.secondary)
                            } else {
                                ForEach(portfolio.holdings) { holding in
                                    VStack(alignment: .leading, spacing: 4) {
                                        HStack {
                                            Text(holding.symbol)
                                                .font(.headline)
                                            if holding.priceStale {
                                                Text("Stale")
                                                    .font(.caption2)
                                                    .padding(.horizontal, 6)
                                                    .padding(.vertical, 2)
                                                    .background(.orange.opacity(0.2), in: Capsule())
                                            }
                                            Spacer()
                                            Text(CurrencyFormatter.format(holding.marketValue))
                                                .font(.headline)
                                        }
                                        Text(holding.name)
                                            .font(.subheadline)
                                            .foregroundStyle(.secondary)
                                        HStack {
                                            Text("\(holding.quantity, format: .number.precision(.fractionLength(0...4))) @ \(CurrencyFormatter.format(holding.currentPrice))")
                                            Spacer()
                                            Text(CurrencyFormatter.format(holding.unrealizedGain))
                                                .foregroundStyle(holding.unrealizedGain >= 0 ? .green : .red)
                                        }
                                        .font(.caption)
                                        .foregroundStyle(.secondary)
                                    }
                                    .padding(.vertical, 4)
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("Portfolio")
            .refreshable {
                await loadPortfolio()
            }
            .task {
                await loadPortfolio()
            }
        }
    }

    @ViewBuilder
    private func summaryRow(title: String, value: String, valueColor: Color = .primary) -> some View {
        HStack {
            Text(title)
            Spacer()
            Text(value)
                .foregroundStyle(valueColor)
        }
    }

    private func loadPortfolio() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            portfolio = try await portfolioService.getPortfolio()
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

#Preview {
    PortfolioView()
}
