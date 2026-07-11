import Foundation

enum CurrencyFormatter {
    private static let formatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "EUR"
        formatter.maximumFractionDigits = 2
        return formatter
    }()

    static func format(_ value: Double, currencyCode: String = "EUR") -> String {
        formatter.currencyCode = currencyCode
        return formatter.string(from: NSNumber(value: value)) ?? String(format: "%.2f", value)
    }
}

enum DateFormatterHelper {
    private static let isoFormatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return formatter
    }()

    private static let fallbackISOFormatter = ISO8601DateFormatter()

    private static let displayFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter
    }()

    static func displayDate(_ value: String) -> String {
        if let date = isoFormatter.date(from: value) ?? fallbackISOFormatter.date(from: value) {
            return displayFormatter.string(from: date)
        }
        return value
    }
}
