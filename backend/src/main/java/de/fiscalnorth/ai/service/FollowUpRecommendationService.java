package de.fiscalnorth.ai.service;

import de.fiscalnorth.ai.dto.ProposedAction;
import de.fiscalnorth.ai.dto.ProposedActionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class FollowUpRecommendationService {

    private static final int MAX_RECOMMENDATIONS = 3;
    private static final int MAX_LENGTH = 140;

    public List<String> resolve(List<String> fromModel, List<ProposedAction> actions) {
        List<String> sanitized = sanitize(fromModel);
        if (!sanitized.isEmpty()) {
            return sanitized;
        }
        return defaultsFor(actions);
    }

    private List<String> sanitize(List<String> fromModel) {
        if (fromModel == null || fromModel.isEmpty()) {
            return List.of();
        }
        Set<String> seen = new LinkedHashSet<>();
        for (String item : fromModel) {
            if (item == null) {
                continue;
            }
            String trimmed = item.trim();
            if (trimmed.length() < 8 || trimmed.length() > MAX_LENGTH) {
                continue;
            }
            seen.add(trimmed);
            if (seen.size() >= MAX_RECOMMENDATIONS) {
                break;
            }
        }
        return List.copyOf(seen);
    }

    private List<String> defaultsFor(List<ProposedAction> actions) {
        if (actions != null && !actions.isEmpty()) {
            boolean hasBudget = actions.stream().anyMatch(a -> a.type() == ProposedActionType.CREATE_BUDGET);
            boolean hasCategory = actions.stream().anyMatch(a -> a.type() == ProposedActionType.CREATE_CATEGORY);
            boolean hasTransaction = actions.stream().anyMatch(a -> a.type() == ProposedActionType.CREATE_TRANSACTION);
            List<String> followUps = new ArrayList<>();
            if (hasBudget) {
                followUps.add("Welches Budget ist diesen Monat am stärksten ausgelastet?");
            }
            if (hasCategory) {
                followUps.add("Zeig mir die größten Ausgaben in dieser Kategorie.");
            }
            if (hasTransaction) {
                followUps.add("Wie hat sich mein Kontostand in den letzten Wochen entwickelt?");
            }
            followUps.add("Gibt es noch etwas, das ich für dich vorbereiten soll?");
            return sanitize(followUps);
        }
        return List.of(
                "Wo gebe ich diesen Monat am meisten aus?",
                "Welche Budgets sind fast aufgebraucht?",
                "Was war meine letzte größere Ausgabe?");
    }
}
