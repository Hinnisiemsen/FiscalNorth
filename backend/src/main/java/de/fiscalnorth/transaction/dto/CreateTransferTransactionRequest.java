package de.fiscalnorth.transaction.dto;

import de.fiscalnorth.transaction.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransferTransactionRequest(
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        TransactionType transactionType,
        Long fromAccountId,
        Long toAccountId) {
}
