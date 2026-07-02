package de.fiscalnorth.goal.dto;

import de.fiscalnorth.goal.model.GoalStatus;
import de.fiscalnorth.goal.model.GoalType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateGoalRequest(
        String name,
        GoalType goalType,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate,
        Long linkedAccountId,
        BigDecimal monthlyContribution,
        GoalStatus status) {
}
