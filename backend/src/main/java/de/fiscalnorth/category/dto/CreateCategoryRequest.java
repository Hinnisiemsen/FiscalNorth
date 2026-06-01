package de.fiscalnorth.category.dto;

import de.fiscalnorth.transaction.model.TransactionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotNull
        String name,

        @Valid
        TransactionType transactionType
) {
}
