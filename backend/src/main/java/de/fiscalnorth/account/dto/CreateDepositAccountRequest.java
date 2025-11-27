package de.fiscalnorth.account.dto;

import de.fiscalnorth.shared.SupportedCurrency;
import java.math.BigDecimal;

public record CreateDepositAccountRequest(
        String name,
        SupportedCurrency currency,
        BigDecimal balance,
        Double interestRate,
        String term,
        Boolean renewable) {
}
