package de.fiscalnorth.account.dto;

import de.fiscalnorth.account.model.AccountType;
import de.fiscalnorth.shared.SupportedCurrency;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateBankAccountRequest(
        @NotNull(message = "{validation.bankAccount.nameRequired}")
        String bankName,

        @NotNull(message = "{validation.bankAccount.ibanRequired}")
        @Size(min = 15, max = 34)
        String iban,

        @NotNull(message = "{validation.bankAccount.bicRequired}")
        @Pattern(
                regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$",
                message = "{validation.bankAccount.bicFormat}"
        )
        String bic,

        @NotNull(message = "{validation.bankAccount.accountTypeRequired}")
        AccountType accountType,

        String name,

        SupportedCurrency currency,

        @PositiveOrZero
        BigDecimal balance
) {}
