package de.hinni.haushaltsmanager.account.model;

public enum AccountType {
    CHECKING,        // Girokonto / Checking account
    SAVINGS,         // Sparkonto / Savings account
    CREDIT_CARD,     // Kreditkarte
    CASH,            // Bargeld / Cash wallet
    INVESTMENT,      // Depot (stocks, ETFs, etc.)
    LOAN,            // Kreditkonto / Loan account
    PAYPAL,          // PayPal & Payment Services
    CRYPTO,          // Kryptowallet
    PENSION,         // Altersvorsorge (e.g., Riester, Rürup, 401(k))
    INSURANCE,       // Versicherungskonten
    BUSINESS,        // Geschäftskonto
    PREPAID,         // Prepaid-Karten
    OTHER            // Sonstige Konten
}
