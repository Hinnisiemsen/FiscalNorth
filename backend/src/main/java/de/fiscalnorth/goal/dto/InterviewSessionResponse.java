package de.fiscalnorth.goal.dto;

import de.fiscalnorth.goal.model.InterviewSessionStatus;

public record InterviewSessionResponse(
        Long id,
        InterviewSessionStatus status,
        String answersJson,
        GoalPlanResponse plan) {
}
