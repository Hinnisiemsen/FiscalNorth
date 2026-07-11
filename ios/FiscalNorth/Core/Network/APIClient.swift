import Foundation

enum APIError: LocalizedError {
    case invalidURL
    case invalidResponse
    case httpError(statusCode: Int, message: String?)
    case decodingError(Error)
    case unauthorized

    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid API URL."
        case .invalidResponse:
            return "Invalid server response."
        case .httpError(let statusCode, let message):
            if let message, !message.isEmpty {
                return "Request failed (\(statusCode)): \(message)"
            }
            return "Request failed with status \(statusCode)."
        case .decodingError(let error):
            return "Failed to decode response: \(error.localizedDescription)"
        case .unauthorized:
            return "Session expired. Please sign in again."
        }
    }
}

enum HTTPMethod: String {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case delete = "DELETE"
}

actor APIClient {
    static let shared = APIClient()

    private let session: URLSession
    private var csrfToken: String?
    private var csrfHeaderName: String = "X-XSRF-TOKEN"

    init(session: URLSession = .shared) {
        self.session = session
    }

    func get<T: Decodable>(_ path: String) async throws -> T {
        try await request(path: path, method: .get)
    }

    func post<T: Decodable>(_ path: String, body: Encodable? = nil) async throws -> T {
        try await request(path: path, method: .post, body: body)
    }

    func postEmpty(_ path: String) async throws {
        let _: EmptyResponse = try await request(path: path, method: .post)
    }

    func put<T: Decodable>(_ path: String, body: Encodable) async throws -> T {
        try await request(path: path, method: .put, body: body)
    }

    func delete(_ path: String) async throws {
        let _: EmptyResponse = try await request(path: path, method: .delete)
    }

    func clearSession() {
        csrfToken = nil
        if let cookies = HTTPCookieStorage.shared.cookies(for: APIConfiguration.baseURL) {
            cookies.forEach { HTTPCookieStorage.shared.deleteCookie($0) }
        }
    }

    private func request<T: Decodable>(
        path: String,
        method: HTTPMethod,
        body: Encodable? = nil
    ) async throws -> T {
        if method != .get, csrfToken == nil {
            try await refreshCSRFToken()
        }

        let url = APIConfiguration.apiURL(path: path)
        var request = URLRequest(url: url)
        request.httpMethod = method.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        if let body {
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.httpBody = try JSONEncoder.api.encode(body)
        }

        if method != .get, let csrfToken {
            request.setValue(csrfToken, forHTTPHeaderField: csrfHeaderName)
        }

        let (data, response) = try await session.data(for: request)
        try validate(response: response, data: data)

        if T.self == EmptyResponse.self || data.isEmpty {
            return EmptyResponse() as! T
        }

        do {
            return try JSONDecoder.api.decode(T.self, from: data)
        } catch {
            throw APIError.decodingError(error)
        }
    }

    private func validate(response: URLResponse, data: Data) throws {
        guard let http = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }

        if http.statusCode == 401 {
            clearSession()
            throw APIError.unauthorized
        }

        guard (200 ... 299).contains(http.statusCode) else {
            let message = String(data: data, encoding: .utf8)
            throw APIError.httpError(statusCode: http.statusCode, message: message)
        }
    }

    private func refreshCSRFToken() async throws {
        let url = APIConfiguration.apiURL(path: "auth/csrf")
        var request = URLRequest(url: url)
        request.httpMethod = HTTPMethod.get.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        let (data, response) = try await session.data(for: request)
        try validate(response: response, data: data)

        let tokenResponse = try JSONDecoder.api.decode(CsrfTokenResponse.self, from: data)
        csrfToken = tokenResponse.token
        csrfHeaderName = tokenResponse.headerName

        if csrfToken == nil,
           let cookies = HTTPCookieStorage.shared.cookies(for: APIConfiguration.baseURL) {
            csrfToken = cookies.first(where: { $0.name == "XSRF-TOKEN" })?.value
        }
    }
}

private struct EmptyResponse: Decodable {
    init() {}
}

private extension JSONDecoder {
    static let api: JSONDecoder = {
        let decoder = JSONDecoder()
        return decoder
    }()
}

private extension JSONEncoder {
    static let api: JSONEncoder = {
        let encoder = JSONEncoder()
        return encoder
    }()
}
