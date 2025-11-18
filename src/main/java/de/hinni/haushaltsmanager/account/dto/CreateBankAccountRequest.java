package de.hinni.haushaltsmanager.account.dto;

import de.hinni.haushaltsmanager.account.model.AccountType;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record CreateBankAccountRequest(
        @NotNull(message = "Name can't be empty")
        String bankName,

        @NotNull(message = "IBAN is mandatory")
        @Size(min = 15, max = 34)
        String iban,

        @NotNull(message = "BIC is mandatory")
        @Pattern(
                regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$",
                message = "BIC muss 8 oder 11 Zeichen lang sein (Großbuchstaben/Zahlen)"
        )
        String bic,

        @NotNull(message = "Please select an Account Type")
        AccountType accountType
) {}
