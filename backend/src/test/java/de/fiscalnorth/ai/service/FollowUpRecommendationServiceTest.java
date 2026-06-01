package de.fiscalnorth.ai.service;

import de.fiscalnorth.ai.dto.ProposedAction;
import de.fiscalnorth.ai.dto.ProposedActionType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FollowUpRecommendationServiceTest {

    private final FollowUpRecommendationService service = new FollowUpRecommendationService();

    @Test
    void sanitizeTrimsAndCapsRecommendations() {
        List<String> result = service.resolve(
                List.of(
                        "  Wie hoch ist mein Sparkonto?  ",
                        "kurz",
                        "Welche Verträge laufen dieses Jahr aus?",
                        "Noch eine vierte Frage, die abgeschnitten werden sollte"),
                List.of());

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("Wie hoch ist mein Sparkonto?");
    }

    @Test
    void defaultsWhenModelReturnsNothing() {
        List<String> result = service.resolve(List.of(), List.of());

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).contains("aus");
    }

    @Test
    void defaultsReflectProposedActions() {
        List<String> result = service.resolve(
                List.of(),
                List.of(new ProposedAction(
                        ProposedActionType.CREATE_BUDGET,
                        "Budget",
                        Map.of("name", "Transport", "limit", 200, "startDate", "2026-06-01", "endDate", "2026-06-30"))));

        assertThat(result).anyMatch(s -> s.toLowerCase().contains("budget"));
    }
}
