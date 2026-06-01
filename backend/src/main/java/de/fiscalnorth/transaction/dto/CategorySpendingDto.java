package de.fiscalnorth.transaction.dto;

import java.math.BigDecimal;

public record CategorySpendingDto(String categoryName, BigDecimal amount) {
}
