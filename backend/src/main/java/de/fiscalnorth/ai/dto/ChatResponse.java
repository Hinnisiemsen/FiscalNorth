package de.fiscalnorth.ai.dto;

import java.util.List;

public record ChatResponse(
        String reply,
        List<ProposedAction> proposedActions,
        List<String> followUpRecommendations,
        String conversationId
) {
}
