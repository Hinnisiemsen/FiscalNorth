package de.fiscalnorth.account.dto;

import de.fiscalnorth.shared.SupportedCurrency;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateDepositAccountRequest(
                @NotBlank String name,
                @NotNull SupportedCurrency currency,
                @NotNull @PositiveOrZero BigDecimal balance,
                @NotNull @PositiveOrZero Double interestRate,
                @NotBlank String term,
                @NotNull Boolean renewable) {
}
