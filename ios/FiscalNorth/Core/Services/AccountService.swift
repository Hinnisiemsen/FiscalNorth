import Foundation

struct AccountService {
    private let api = APIClient.shared

    func getAllAccounts() async throws -> [UnifiedAccount] {
        async let deposits: [DepositAccount] = api.get("account/deposit")
        async let banks: [BankAccount] = api.get("account/bank")
        async let cryptos: [CryptoAccount] = api.get("account/crypto")

        let (depositList, bankList, cryptoList) = try await (deposits, banks, cryptos)

        return depositList.map { account in
            UnifiedAccount(
                id: account.id,
                kind: .deposit,
                name: account.name,
                currency: account.currency,
                balance: account.balance,
                subtitle: account.term
            )
        } + bankList.map { account in
            UnifiedAccount(
                id: account.id,
                kind: .bank,
                name: account.name,
                currency: account.currency,
                balance: account.balance,
                subtitle: account.bankName
            )
        } + cryptoList.map { account in
            UnifiedAccount(
                id: account.id,
                kind: .crypto,
                name: account.name,
                currency: account.currency,
                balance: account.balance,
                subtitle: account.walletAddress
            )
        }
    }

    func totalBalance() async throws -> Double {
        try await getAllAccounts().reduce(0) { $0 + $1.balance }
    }
}
