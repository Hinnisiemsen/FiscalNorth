package de.fiscalnorth.goal.dto;

import de.fiscalnorth.goal.model.GoalStatus;
import de.fiscalnorth.goal.model.GoalType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalWithProgress(
        Long id,
        String name,
        GoalType goalType,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate,
        Long linkedAccountId,
        String linkedAccountName,
        BigDecimal monthlyContribution,
        GoalStatus status,
        BigDecimal progressAmount,
        BigDecimal progressPercent,
        BigDecimal remainingAmount,
        Long daysRemaining,
        boolean onTrack) {
}
