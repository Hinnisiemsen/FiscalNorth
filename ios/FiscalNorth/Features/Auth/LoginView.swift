import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var authService: AuthService

    @State private var email = ""
    @State private var password = ""
    @State private var userName = ""
    @State private var isRegisterMode = false
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Email", text: $email)
                        .textInputAutocapitalization(.never)
                        .keyboardType(.emailAddress)
                        .autocorrectionDisabled()

                    SecureField("Password", text: $password)

                    if isRegisterMode {
                        TextField("Display name", text: $userName)
                    }
                } header: {
                    Text(isRegisterMode ? "Create account" : "Sign in")
                } footer: {
                    Text("Demo: alex@fiscalnorth.local / demo1234")
                        .font(.footnote)
                }

                if let errorMessage {
                    Section {
                        Text(errorMessage)
                            .foregroundStyle(.red)
                            .font(.footnote)
                    }
                }

                Section {
                    Button(isRegisterMode ? "Register" : "Sign in") {
                        Task { await submit() }
                    }
                    .disabled(authService.isLoading || !canSubmit)

                    Button(isRegisterMode ? "Already have an account? Sign in" : "Need an account? Register") {
                        isRegisterMode.toggle()
                        errorMessage = nil
                    }
                }
            }
            .navigationTitle("FiscalNorth")
            .overlay {
                if authService.isLoading {
                    ProgressView()
                        .controlSize(.large)
                }
            }
        }
    }

    private var canSubmit: Bool {
        !email.isEmpty && !password.isEmpty && (!isRegisterMode || !userName.isEmpty)
    }

    private func submit() async {
        errorMessage = nil
        do {
            if isRegisterMode {
                try await authService.register(userName: userName, email: email, password: password)
            } else {
                try await authService.login(email: email, password: password)
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

#Preview {
    LoginView()
        .environmentObject(AuthService())
}
