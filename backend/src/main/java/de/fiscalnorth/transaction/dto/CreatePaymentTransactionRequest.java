package de.fiscalnorth.transaction.dto;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentTransactionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotBlank String description,
        @NotNull LocalDate transactionDate,
        @NotNull TransactionType transactionType,
        String tags,
        Category category,
        Contract contract) {
}
