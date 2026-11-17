package de.fiscalnorth.ai.service;

import de.fiscalnorth.ai.dto.ChatResponse;
import de.fiscalnorth.ai.dto.ProposedAction;
import de.fiscalnorth.ai.dto.ProposedActionType;
import de.fiscalnorth.shared.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Offline replies when {@code GEMINI_API_KEY} is unset so the local demo still
 * exercises the Gemini-style chat UI.
 */
@Component
@RequiredArgsConstructor
public class DemoAssistantResponder {

    private final Messages messages;

    public ChatResponse respond(String message, String conversationId) {
        String convId = conversationId != null && !conversationId.isBlank()
                ? conversationId
                : UUID.randomUUID().toString();
        String text = message == null ? "" : message.toLowerCase(Locale.ROOT);

        if (looksLikeBudgetRequest(text)) {
            YearMonth month = YearMonth.now();
            ProposedAction action = new ProposedAction(
                    ProposedActionType.CREATE_BUDGET,
                    messages.get("assistant.demo.action.transportBudget"),
                    Map.of(
                            "name", messages.get("assistant.demo.budgetName.transport"),
                            "limit", 120,
                            "startDate", month.atDay(1).toString(),
                            "endDate", month.atEndOfMonth().toString()));
            return new ChatResponse(
                    messages.get("assistant.demo.reply.budget"),
                    List.of(action),
                    List.of(
                            messages.get("assistant.demo.followUp.groceries"),
                            messages.get("assistant.demo.followUp.spending")),
                    convId);
        }

        if (looksLikeGroceryQuestion(text)) {
            return new ChatResponse(
                    messages.get("assistant.demo.reply.groceries"),
                    List.of(),
                    List.of(
                            messages.get("assistant.demo.followUp.spending"),
                            messages.get("assistant.demo.followUp.budget")),
                    convId);
        }

        return new ChatResponse(
                messages.get("assistant.demo.reply.default"),
                List.of(),
                List.of(
                        messages.get("assistant.demo.followUp.groceries"),
                        messages.get("assistant.demo.followUp.budget"),
                        messages.get("assistant.demo.followUp.spending")),
                convId);
    }

    private static boolean looksLikeBudgetRequest(String text) {
        return text.contains("budget") || text.contains("transport") || text.contains("limit");
    }

    private static boolean looksLikeGroceryQuestion(String text) {
        return text.contains("grocer")
                || text.contains("lebensmittel")
                || text.contains("einkauf")
                || text.contains("comida")
                || text.contains("courses");
    }
}
