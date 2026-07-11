package de.fiscalnorth.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SplitLineRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull Long categoryId,
        String note) {}
