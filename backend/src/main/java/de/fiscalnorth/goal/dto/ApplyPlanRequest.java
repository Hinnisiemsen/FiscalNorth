package de.fiscalnorth.goal.dto;

import java.util.List;

public record ApplyPlanRequest(List<RecommendedGoalDto> goals) {
}
