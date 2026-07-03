package de.fiscalnorth.goal.dto;

import de.fiscalnorth.goal.model.GoalType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalRequest(
        @NotBlank String name,
        @NotNull GoalType goalType,
        @NotNull @Positive BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate,
        Long linkedAccountId,
        BigDecimal monthlyContribution) {
}
