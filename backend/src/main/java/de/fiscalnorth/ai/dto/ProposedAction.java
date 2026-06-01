package de.fiscalnorth.ai.dto;

import java.util.Map;

public record ProposedAction(
        ProposedActionType type,
        String summary,
        Map<String, Object> payload
) {
}
