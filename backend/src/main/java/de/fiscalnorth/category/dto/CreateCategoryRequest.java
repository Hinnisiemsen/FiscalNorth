package de.fiscalnorth.category.dto;

import de.fiscalnorth.transaction.model.TransactionType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record CreateCategoryRequest(
        @NotNull
        String name,

        @Valid
        TransactionType transactionType
) {
}
