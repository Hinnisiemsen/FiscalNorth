package de.fiscalnorth.goal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.ai.client.GeminiClient;
import de.fiscalnorth.ai.config.AiProperties;
import de.fiscalnorth.ai.service.FinancialContextService;
import de.fiscalnorth.goal.dto.GoalPlanResponse;
import de.fiscalnorth.goal.dto.RecommendedGoalDto;
import de.fiscalnorth.goal.model.GoalType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GoalPlanningService {

    private static final Pattern JSON_BLOCK =
            Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    private final GeminiClient geminiClient;
    private final FinancialContextService financialContextService;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    public GoalPlanResponse generatePlan(Map<String, Object> answers) {
        if (!isAvailable()) {
            return buildFallbackPlan(answers);
        }

        String context = financialContextService.buildContextSnapshot();
        String systemPrompt = """
                Du bist der Finanzplanungs-Assistent von Fiscal North.
                Erstelle einen realistischen Sparplan basierend auf den Interview-Antworten und Finanzdaten.
                Antworte AUSSCHLIESSLICH mit gültigem JSON:
                {
                  "summary": "Kurze Zusammenfassung auf Deutsch (2-3 Sätze)",
                  "recommendedGoals": [
                    {
                      "name": "Zielname",
                      "goalType": "EMERGENCY_FUND|VACATION|HOME|DEBT_PAYOFF|RETIREMENT|OTHER",
                      "targetAmount": 5000,
                      "targetDate": "2027-06-01",
                      "linkedAccountId": null,
                      "monthlyContribution": 200,
                      "rationale": "Warum dieses Ziel sinnvoll ist"
                    }
                  ],
                  "monthlySavingsTarget": 350,
                  "insights": ["Tipp 1", "Tipp 2"]
                }

                Nutze realistische Beträge basierend auf verfügbarem Einkommen.
                goalType muss einer der Enum-Werte sein.
                targetDate im Format YYYY-MM-DD oder null.

                Finanzdaten:
                """ + context + "\n\nInterview-Antworten:\n" + answers;

        String raw = geminiClient.generate(systemPrompt, "Erstelle meinen Finanzziel-Plan.");
        return parsePlanResponse(raw, answers);
    }

    private boolean isAvailable() {
        return aiProperties.enabled() && apiKey != null && !apiKey.isBlank();
    }

    private GoalPlanResponse parsePlanResponse(String raw, Map<String, Object> answers) {
        if (raw == null || raw.isBlank()) {
            return buildFallbackPlan(answers);
        }
        String json = raw.trim();
        Matcher m = JSON_BLOCK.matcher(json);
        if (m.find()) {
            json = m.group(1).trim();
        } else if (json.contains("{")) {
            json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            String summary = root.path("summary").asText("Dein persönlicher Sparplan");
            BigDecimal monthlySavingsTarget = parseDecimal(root.path("monthlySavingsTarget"));

            List<RecommendedGoalDto> goals = new ArrayList<>();
            JsonNode goalsNode = root.path("recommendedGoals");
            if (goalsNode.isArray()) {
                for (JsonNode g : goalsNode) {
                    GoalType type = parseGoalType(g.path("goalType").asText("OTHER"));
                    LocalDate targetDate = g.hasNonNull("targetDate") && !g.path("targetDate").asText().isBlank()
                            ? LocalDate.parse(g.path("targetDate").asText())
                            : null;
                    Long linkedAccountId = g.hasNonNull("linkedAccountId") && !g.path("linkedAccountId").isNull()
                            ? g.path("linkedAccountId").asLong()
                            : null;
                    goals.add(new RecommendedGoalDto(
                            g.path("name").asText("Sparziel"),
                            type,
                            parseDecimal(g.path("targetAmount")),
                            targetDate,
                            linkedAccountId,
                            parseDecimal(g.path("monthlyContribution")),
                            g.path("rationale").asText("")));
                }
            }

            List<String> insights = new ArrayList<>();
            JsonNode insightsNode = root.path("insights");
            if (insightsNode.isArray()) {
                insightsNode.forEach(n -> insights.add(n.asText()));
            }

            if (goals.isEmpty()) {
                return buildFallbackPlan(answers);
            }
            return new GoalPlanResponse(summary, goals, monthlySavingsTarget, insights);
        } catch (Exception e) {
            return buildFallbackPlan(answers);
        }
    }

    @SuppressWarnings("unchecked")
    private GoalPlanResponse buildFallbackPlan(Map<String, Object> answers) {
        List<RecommendedGoalDto> goals = new ArrayList<>();
        BigDecimal monthlyWilling = parseDecimalFromAnswers(answers, "monthlyWillingToSave", BigDecimal.valueOf(200));

        Object priorities = answers.get("priorities");
        if (priorities instanceof List<?> list && !list.isEmpty()) {
            BigDecimal perGoal = monthlyWilling.divide(
                    BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
            for (Object item : list) {
                String typeStr = item.toString();
                GoalType type = parseGoalType(typeStr);
                String name = goalTypeLabel(type);
                BigDecimal target = parseTargetForType(answers, typeStr);
                goals.add(new RecommendedGoalDto(
                        name,
                        type,
                        target,
                        LocalDate.now().plusYears(1),
                        null,
                        perGoal,
                        "Basierend auf deinen Prioritäten"));
            }
        } else {
            goals.add(new RecommendedGoalDto(
                    "Notgroschen",
                    GoalType.EMERGENCY_FUND,
                    BigDecimal.valueOf(5000),
                    LocalDate.now().plusMonths(12),
                    null,
                    monthlyWilling,
                    "Empfohlenes Startziel für finanzielle Sicherheit"));
        }

        return new GoalPlanResponse(
                "Basierend auf deinen Angaben haben wir einen Sparplan erstellt. "
                        + "Passe die Ziele nach Bedarf an.",
                goals,
                monthlyWilling,
                List.of(
                        "Beginne mit dem wichtigsten Ziel und erhöhe Sparraten schrittweise.",
                        "Verknüpfe Ziele mit Sparkonten für automatische Fortschrittsverfolgung."));
    }

    private BigDecimal parseTargetForType(Map<String, Object> answers, String typeStr) {
        Object targets = answers.get("targets");
        if (targets instanceof Map<?, ?> map) {
            Object val = map.get(typeStr);
            if (val != null) {
                return new BigDecimal(val.toString());
            }
        }
        return switch (parseGoalType(typeStr)) {
            case EMERGENCY_FUND -> BigDecimal.valueOf(5000);
            case VACATION -> BigDecimal.valueOf(3000);
            case HOME -> BigDecimal.valueOf(20000);
            case DEBT_PAYOFF -> BigDecimal.valueOf(10000);
            case RETIREMENT -> BigDecimal.valueOf(50000);
            case OTHER -> BigDecimal.valueOf(5000);
        };
    }

    private BigDecimal parseDecimalFromAnswers(Map<String, Object> answers, String key, BigDecimal fallback) {
        Object val = answers.get(key);
        if (val == null) {
            return fallback;
        }
        try {
            return new BigDecimal(val.toString());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private GoalType parseGoalType(String value) {
        try {
            return GoalType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GoalType.OTHER;
        }
    }

    private BigDecimal parseDecimal(JsonNode node) {
        if (node == null || node.isNull() || node.asText().isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(node.asText());
    }

    private String goalTypeLabel(GoalType type) {
        return switch (type) {
            case EMERGENCY_FUND -> "Notgroschen";
            case VACATION -> "Urlaub";
            case HOME -> "Eigenheim";
            case DEBT_PAYOFF -> "Schulden tilgen";
            case RETIREMENT -> "Altersvorsorge";
            case OTHER -> "Sparziel";
        };
    }
}
