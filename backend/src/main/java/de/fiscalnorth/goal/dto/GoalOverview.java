package de.fiscalnorth.goal.dto;

import java.math.BigDecimal;

public record GoalOverview(
        int totalGoals,
        int activeGoals,
        int completedCount,
        BigDecimal overallProgressPercent,
        BigDecimal totalRemaining) {
}
