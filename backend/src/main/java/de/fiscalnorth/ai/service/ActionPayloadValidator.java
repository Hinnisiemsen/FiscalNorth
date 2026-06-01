package de.fiscalnorth.ai.service;

import de.fiscalnorth.ai.dto.ProposedAction;
import de.fiscalnorth.ai.dto.ProposedActionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ActionPayloadValidator {

    public List<ProposedAction> validateAndNormalize(List<ProposedAction> actions) {
        if (actions == null) {
            return List.of();
        }
        List<ProposedAction> valid = new ArrayList<>();
        for (ProposedAction action : actions) {
            if (action == null || action.type() == null) {
                continue;
            }
            try {
                valid.add(switch (action.type()) {
                    case CREATE_BUDGET -> validateBudget(action);
                    case CREATE_CATEGORY -> validateCategory(action);
                    case CREATE_TRANSACTION -> validateTransaction(action);
                });
            } catch (IllegalArgumentException ignored) {
                // skip invalid proposals
            }
        }
        return valid;
    }

    private ProposedAction validateBudget(ProposedAction action) {
        Map<String, Object> p = action.payload();
        String name = requireString(p, "name");
        BigDecimal limit = requirePositiveDecimal(p, "limit");
        LocalDate start = requireDate(p, "startDate");
        LocalDate end = requireDate(p, "endDate");
        Map<String, Object> normalized = new java.util.HashMap<>();
        normalized.put("name", name);
        normalized.put("limit", limit);
        normalized.put("startDate", start.toString());
        normalized.put("endDate", end.toString());
        if (p.get("categoryId") != null) {
            normalized.put("categoryId", p.get("categoryId"));
        }
        String summary = enrichSummary(action.summary(), buildBudgetSummary(name, limit, start, end));
        return new ProposedAction(ProposedActionType.CREATE_BUDGET, summary, normalized);
    }

    private ProposedAction validateCategory(ProposedAction action) {
        Map<String, Object> p = action.payload();
        String name = requireString(p, "name");
        String type = requireString(p, "transactionType");
        if (!type.equalsIgnoreCase("Expense") && !type.equalsIgnoreCase("Income")) {
            throw new IllegalArgumentException("invalid transactionType");
        }
        String normalizedType = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
        String summary = enrichSummary(action.summary(), buildCategorySummary(name, normalizedType));
        return new ProposedAction(
                ProposedActionType.CREATE_CATEGORY,
                summary,
                Map.of("name", name, "transactionType", normalizedType));
    }

    private ProposedAction validateTransaction(ProposedAction action) {
        Map<String, Object> p = action.payload();
        BigDecimal amount = requirePositiveDecimal(p, "amount");
        String description = requireString(p, "description");
        LocalDate date = requireDate(p, "transactionDate");
        String type = requireString(p, "transactionType");
        if (!type.equalsIgnoreCase("Expense") && !type.equalsIgnoreCase("Income")) {
            throw new IllegalArgumentException("invalid transactionType");
        }
        Map<String, Object> normalized = new java.util.HashMap<>();
        normalized.put("amount", amount);
        normalized.put("description", description);
        normalized.put("transactionDate", date.toString());
        normalized.put("transactionType", type.equalsIgnoreCase("Income") ? "Income" : "Expense");
        if (p.get("categoryId") != null) {
            normalized.put("categoryId", p.get("categoryId"));
        }
        String txType = type.equalsIgnoreCase("Income") ? "Income" : "Expense";
        String summary = enrichSummary(action.summary(), buildTransactionSummary(description, amount, date, txType));
        return new ProposedAction(ProposedActionType.CREATE_TRANSACTION, summary, normalized);
    }

    private String requireString(Map<String, Object> p, String key) {
        Object v = p.get(key);
        if (v == null || v.toString().isBlank()) {
            throw new IllegalArgumentException("missing " + key);
        }
        return v.toString().trim();
    }

    private BigDecimal requirePositiveDecimal(Map<String, Object> p, String key) {
        Object v = p.get(key);
        if (v == null) {
            throw new IllegalArgumentException("missing " + key);
        }
        BigDecimal d = new BigDecimal(v.toString());
        if (d.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("must be positive");
        }
        return d;
    }

    private LocalDate requireDate(Map<String, Object> p, String key) {
        return LocalDate.parse(requireString(p, key));
    }

    private String enrichSummary(String fromModel, String fallback) {
        if (fromModel != null && fromModel.trim().length() >= 20) {
            return fromModel.trim();
        }
        return fallback;
    }

    private String buildBudgetSummary(String name, BigDecimal limit, LocalDate start, LocalDate end) {
        return "Fiscal North legt das Budget „%s“ mit %.2f € für den Zeitraum %s bis %s an."
                .formatted(name, limit, start, end);
    }

    private String buildCategorySummary(String name, String type) {
        String art = "Income".equals(type) ? "Einnahme" : "Ausgabe";
        return "Fiscal North legt die Kategorie „%s“ als %s an.".formatted(name, art);
    }

    private String buildTransactionSummary(String description, BigDecimal amount, LocalDate date, String type) {
        String art = "Income".equals(type) ? "Einnahme" : "Ausgabe";
        return "Fiscal North erfasst die %s „%s“ über %.2f € am %s.".formatted(art, description, amount, date);
    }
}
