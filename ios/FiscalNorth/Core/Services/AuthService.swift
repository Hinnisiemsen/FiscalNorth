import Foundation

@MainActor
final class AuthService: ObservableObject {
    @Published private(set) var currentUser: UserProfile?
    @Published private(set) var isAuthenticated = false
    @Published private(set) var isLoading = false

    private let api = APIClient.shared

    func bootstrap() async {
        isLoading = true
        defer { isLoading = false }

        do {
            let status: AuthStatus = try await api.get("auth/status")
            if status.authenticated {
                currentUser = try await api.get("user/me")
                isAuthenticated = true
            } else {
                resetSession()
            }
        } catch APIError.unauthorized {
            resetSession()
        } catch {
            resetSession()
        }
    }

    func login(email: String, password: String) async throws {
        isLoading = true
        defer { isLoading = false }

        let profile: UserProfile = try await api.post("auth/login", body: LoginRequest(email: email, password: password))
        currentUser = profile
        isAuthenticated = true
    }

    func register(userName: String, email: String, password: String) async throws {
        isLoading = true
        defer { isLoading = false }

        let profile: UserProfile = try await api.post(
            "auth/register",
            body: RegisterRequest(userName: userName, email: email, password: password)
        )
        currentUser = profile
        isAuthenticated = true
    }

    func logout() async {
        isLoading = true
        defer { isLoading = false }

        do {
            try await api.postEmpty("auth/logout")
        } catch {
            // Clear local session even if the server call fails.
        }
        await api.clearSession()
        resetSession()
    }

    private func resetSession() {
        currentUser = nil
        isAuthenticated = false
    }
}
