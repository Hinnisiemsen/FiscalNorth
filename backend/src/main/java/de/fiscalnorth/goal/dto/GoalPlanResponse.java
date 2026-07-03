package de.fiscalnorth.goal.dto;

import java.math.BigDecimal;
import java.util.List;

public record GoalPlanResponse(
        String summary,
        List<RecommendedGoalDto> recommendedGoals,
        BigDecimal monthlySavingsTarget,
        List<String> insights) {
}
