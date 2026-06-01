package de.fiscalnorth.transaction.dto;

import java.math.BigDecimal;

public record MonthlyTrendDto(int year, int month, String transactionType, BigDecimal amount) {
}
