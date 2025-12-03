package de.fiscalnorth.transaction.dto;

import de.fiscalnorth.transaction.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransferTransactionRequest(
                @NotNull @Positive BigDecimal amount,
                @NotBlank String description,
                @NotNull LocalDate transactionDate,
                @NotNull TransactionType transactionType,
                @NotNull Long fromAccountId,
                @NotNull Long toAccountId) {
}
