package de.fiscalnorth.account.dto;

import de.fiscalnorth.shared.SupportedCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateCryptoAccountRequest(
        @NotBlank String name,
        @NotBlank String walletAddress,
        String provider,
        @NotNull SupportedCurrency currency,
        @NotNull @PositiveOrZero BigDecimal balance
) {}
