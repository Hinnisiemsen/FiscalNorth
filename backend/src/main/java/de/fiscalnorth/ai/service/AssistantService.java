package de.fiscalnorth.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.ai.AiDisabledException;
import de.fiscalnorth.ai.client.GeminiClient;
import de.fiscalnorth.ai.config.AiProperties;
import de.fiscalnorth.ai.dto.ChatResponse;
import de.fiscalnorth.ai.dto.ProposedAction;
import de.fiscalnorth.ai.dto.ProposedActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private static final Pattern JSON_BLOCK = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    private final GeminiClient geminiClient;
    private final FinancialContextService financialContextService;
    private final ActionPayloadValidator actionPayloadValidator;
    private final FollowUpRecommendationService followUpRecommendationService;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    public boolean isAvailable() {
        return aiProperties.enabled() && apiKey != null && !apiKey.isBlank();
    }

    public ChatResponse chat(String message, String conversationId) {
        if (!isAvailable()) {
            throw new AiDisabledException(
                    "Fiscal North ist vorübergehend nicht verfügbar. Bitte später erneut versuchen.");
        }

        String context = financialContextService.buildContextSnapshot();
        String systemPrompt = """
                Du bist der eingebaute Finanzassistent von Fiscal North (persönliche Finanzen).
                Sprich immer als „Fiscal North“ bzw. „wir“ — erwähne niemals Google, Gemini oder andere Anbieter.
                Antworte auf Deutsch, freundlich und klar. Nutze NUR die bereitgestellten Finanzdaten.
                Du darfst KEINE Daten direkt ändern — schlage Änderungen nur als Aktionen vor.

                Format für "reply":
                - Kurze Absätze, durch Leerzeilen getrennt
                - Aufzählungen mit "- " am Zeilenanfang
                - Beträge in EUR mit Komma (z. B. 324,35 €)

                Wenn der Nutzer etwas anlegen möchte, füge passende Einträge in "actions" hinzu, sonst [].

                Für jede Aktion: "summary" = ein vollständiger deutscher Satz für den Nutzer, der genau beschreibt,
                was nach Bestätigung passiert (mit Namen, Beträgen und Zeitraum aus dem payload).

                Antworte AUSSCHLIESSLICH mit gültigem JSON:
                {
                  "reply": "...",
                  "actions": [
                    {
                      "type": "CREATE_BUDGET|CREATE_CATEGORY|CREATE_TRANSACTION",
                      "summary": "Vollständiger Satz auf Deutsch",
                      "payload": { }
                    }
                  ],
                  "followUpRecommendations": [
                    "Kurze Anschlussfrage auf Deutsch",
                    "Zweite Anschlussfrage"
                  ]
                }

                followUpRecommendations: genau 2–3 kurze Fragen, die der Nutzer als Nächstes stellen könnte —
                konkret zum Kontext deiner Antwort, keine Wiederholung der gerade beantworteten Frage.

                Payload-Schemas:
                CREATE_BUDGET: name, limit (number), startDate (YYYY-MM-DD), endDate, optional categoryId
                CREATE_CATEGORY: name, transactionType (Expense|Income)
                CREATE_TRANSACTION: amount, description, transactionDate, transactionType, optional categoryId

                Finanzdaten:
                """ + context;

        String raw = geminiClient.generate(systemPrompt, message);

        ParsedAssistantResponse parsed = parseResponse(raw);
        List<ProposedAction> actions = actionPayloadValidator.validateAndNormalize(parsed.actions());

        String convId = conversationId != null && !conversationId.isBlank()
                ? conversationId
                : UUID.randomUUID().toString();

        List<String> followUps = followUpRecommendationService.resolve(parsed.followUps(), actions);
        return new ChatResponse(parsed.reply(), actions, followUps, convId);
    }

    private ParsedAssistantResponse parseResponse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ParsedAssistantResponse("Keine Antwort vom Modell erhalten.", List.of(), List.of());
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
            String reply = root.path("reply").asText(raw);
            List<ProposedAction> actions = new ArrayList<>();
            JsonNode actionsNode = root.path("actions");
            if (actionsNode.isArray()) {
                for (JsonNode node : actionsNode) {
                    ProposedActionType type = ProposedActionType.valueOf(node.path("type").asText());
                    String summary = node.path("summary").asText("");
                    Map<String, Object> payload = objectMapper.convertValue(
                            node.path("payload"),
                            objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
                    actions.add(new ProposedAction(type, summary, payload));
                }
            }
            List<String> followUps = parseFollowUps(root);
            return new ParsedAssistantResponse(reply, actions, followUps);
        } catch (Exception e) {
            return new ParsedAssistantResponse(raw, List.of(), List.of());
        }
    }

    private List<String> parseFollowUps(JsonNode root) {
        JsonNode node = root.path("followUpRecommendations");
        if (!node.isArray()) {
            node = root.path("followUps");
        }
        if (!node.isArray()) {
            return List.of();
        }
        List<String> followUps = new ArrayList<>();
        for (JsonNode item : node) {
            String text = item.asText("").trim();
            if (!text.isEmpty()) {
                followUps.add(text);
            }
        }
        return followUps;
    }

    private record ParsedAssistantResponse(String reply, List<ProposedAction> actions, List<String> followUps) {
    }
}
