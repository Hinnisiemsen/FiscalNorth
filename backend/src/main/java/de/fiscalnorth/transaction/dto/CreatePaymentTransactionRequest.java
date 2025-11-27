package de.fiscalnorth.transaction.dto;

import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.contract.model.Contract;
import de.fiscalnorth.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentTransactionRequest(
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        TransactionType transactionType,
        String tags,
        Category category,
        Contract contract
) { }
