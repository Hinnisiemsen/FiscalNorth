package de.fiscalnorth.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.ai.client.GeminiClient;
import de.fiscalnorth.ai.config.AiProperties;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.service.EntitlementService;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.service.NotificationService;
import de.fiscalnorth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FinancialOptimizationService {

    private static final Logger log = LoggerFactory.getLogger(FinancialOptimizationService.class);
    private static final Pattern JSON_BLOCK = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    private final GeminiClient geminiClient;
    private final FinancialContextService financialContextService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final EntitlementService entitlementService;

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    public boolean isAvailable() {
        return aiProperties.enabled() && apiKey != null && !apiKey.isBlank();
    }

    public int runOptimizationPass() {
        if (!isAvailable()) {
            log.debug("Skipping AI optimization: assistant not configured");
            return 0;
        }
        int totalCreated = 0;
        for (var user : userRepository.findAll()) {
            if (!entitlementService.hasFeature(user, PremiumFeature.AI_NOTIFICATIONS)) {
                continue;
            }
            totalCreated += runOptimizationPassForOwner(user.getId());
        }
        return totalCreated;
    }

    public int runOptimizationPassForOwner(Long ownerId) {
        if (!isAvailable()) {
            return 0;
        }

        String context = financialContextService.buildContextSnapshotForOwner(ownerId);
        String systemPrompt = """
                Du bist der Finanzoptimierungs-Assistent von Fiscal North.
                Analysiere die Finanzdaten und schlage konkrete, umsetzbare Verbesserungen vor.
                Erwähne niemals Google, Gemini oder andere Anbieter.
                Antworte auf Deutsch.

                Antworte AUSSCHLIESSLICH mit gültigem JSON:
                {
                  "tips": [
                    {
                      "title": "Kurzer Titel",
                      "message": "1–2 Sätze mit konkretem Tipp und Zahlen aus den Daten",
                      "severity": "INFO|WARNING"
                    }
                  ]
                }

                Regeln:
                - Genau 2–4 Tipps
                - Nur Empfehlungen, keine Datenänderungen
                - severity WARNING nur bei echtem Handlungsbedarf

                Finanzdaten:
                """ + context;

        String raw;
        try {
            raw = geminiClient.generate(systemPrompt, "Erstelle heutige Optimierungstipps für den Nutzer.");
        } catch (Exception ex) {
            log.warn("AI optimization pass failed: {}", ex.getMessage());
            return 0;
        }

        List<ParsedTip> tips = parseTips(raw);
        int created = 0;
        String dateKey = LocalDate.now().toString();
        for (int i = 0; i < tips.size(); i++) {
            ParsedTip tip = tips.get(i);
            String dedupeKey = "ai-opt:" + dateKey + ":" + i + ":" + slug(tip.title());
            var saved = notificationService.createIfAbsent(
                    ownerId,
                    dedupeKey,
                    tip.title(),
                    tip.message(),
                    NotificationType.OPTIMIZATION_TIP,
                    tip.severity(),
                    "ai-optimization-cron");
            if (saved.isPresent()) {
                created++;
            }
        }
        return created;
    }

    private List<ParsedTip> parseTips(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String json = raw.trim();
        Matcher matcher = JSON_BLOCK.matcher(json);
        if (matcher.find()) {
            json = matcher.group(1).trim();
        } else if (json.contains("{")) {
            json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode tipsNode = root.path("tips");
            if (!tipsNode.isArray()) {
                return List.of();
            }
            List<ParsedTip> tips = new ArrayList<>();
            for (JsonNode node : tipsNode) {
                String title = node.path("title").asText("").trim();
                String message = node.path("message").asText("").trim();
                if (title.length() < 4 || message.length() < 12) {
                    continue;
                }
                NotificationSeverity severity = "WARNING".equalsIgnoreCase(node.path("severity").asText())
                        ? NotificationSeverity.WARNING
                        : NotificationSeverity.INFO;
                tips.add(new ParsedTip(title, message, severity));
                if (tips.size() >= 4) {
                    break;
                }
            }
            return tips;
        } catch (Exception ex) {
            log.warn("Could not parse optimization JSON: {}", ex.getMessage());
            return List.of();
        }
    }

    private static String slug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9äöüß]+", "-").replaceAll("^-|-$", "");
    }

    private record ParsedTip(String title, String message, NotificationSeverity severity) {
    }
}
