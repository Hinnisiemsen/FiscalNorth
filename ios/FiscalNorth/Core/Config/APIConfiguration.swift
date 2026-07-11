import Foundation

enum APIConfiguration {
    static var baseURL: URL {
        #if DEBUG
        URL(string: "http://localhost:8080")!
        #else
        URL(string: ProcessInfo.processInfo.environment["FISCALNORTH_API_URL"] ?? "https://api.fiscalnorth.example")!
        #endif
    }

    static func apiURL(path: String) -> URL {
        let suffix = path.hasPrefix("/") ? String(path.dropFirst()) : path
        return baseURL.appending(path: "api/\(suffix)")
    }
}
