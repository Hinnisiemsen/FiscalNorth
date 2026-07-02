package de.fiscalnorth.goal.dto;

import de.fiscalnorth.goal.model.GoalType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecommendedGoalDto(
        String name,
        GoalType goalType,
        BigDecimal targetAmount,
        LocalDate targetDate,
        Long linkedAccountId,
        BigDecimal monthlyContribution,
        String rationale) {
}
