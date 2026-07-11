import SwiftUI

@main
struct FiscalNorthApp: App {
    @StateObject private var authService = AuthService()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(authService)
                .task {
                    await authService.bootstrap()
                }
        }
    }
}

struct RootView: View {
    @EnvironmentObject private var authService: AuthService

    var body: some View {
        Group {
            if authService.isLoading && !authService.isAuthenticated {
                ProgressView("Checking session…")
            } else if authService.isAuthenticated {
                MainTabView()
            } else {
                LoginView()
            }
        }
    }
}
