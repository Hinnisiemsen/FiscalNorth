package de.fiscalnorth.ai.service;

import de.fiscalnorth.ai.dto.ProposedAction;
import de.fiscalnorth.ai.dto.ProposedActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ActionPayloadValidatorTest {

    private ActionPayloadValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ActionPayloadValidator();
    }

    @Test
    void validateBudget_normalizesPayload() {
        ProposedAction input = new ProposedAction(
                ProposedActionType.CREATE_BUDGET,
                "short",
                Map.of(
                        "name", " Transport ",
                        "limit", 200,
                        "startDate", "2026-06-01",
                        "endDate", "2026-06-30"));

        List<ProposedAction> result = validator.validateAndNormalize(List.of(input));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).payload().get("name")).isEqualTo("Transport");
        assertThat(result.get(0).payload().get("limit")).isEqualTo(new BigDecimal("200"));
        assertThat(result.get(0).summary()).contains("Transport");
    }

    @Test
    void validateCategory_rejectsInvalidType() {
        ProposedAction input = new ProposedAction(
                ProposedActionType.CREATE_CATEGORY,
                "Kategorie",
                Map.of("name", "Misc", "transactionType", "Transfer"));

        assertThat(validator.validateAndNormalize(List.of(input))).isEmpty();
    }

    @Test
    void validateCategory_normalizesTransactionType() {
        ProposedAction input = new ProposedAction(
                ProposedActionType.CREATE_CATEGORY,
                "ok",
                Map.of("name", "Bonus", "transactionType", "income"));

        List<ProposedAction> result = validator.validateAndNormalize(List.of(input));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).payload().get("transactionType")).isEqualTo("Income");
    }

    @Test
    void validateTransaction_mapsExpenseType() {
        ProposedAction input = new ProposedAction(
                ProposedActionType.CREATE_TRANSACTION,
                "tx",
                Map.of(
                        "amount", "12.50",
                        "description", "Kaffee",
                        "transactionDate", "2026-06-15",
                        "transactionType", "expense"));

        List<ProposedAction> result = validator.validateAndNormalize(List.of(input));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).payload().get("transactionType")).isEqualTo("Expense");
    }

    @Test
    void nullActions_returnsEmptyList() {
        assertThat(validator.validateAndNormalize(null)).isEmpty();
    }

    @Test
    void validateGoal_normalizesPayload() {
        ProposedAction input = new ProposedAction(
                ProposedActionType.CREATE_GOAL,
                "short",
                Map.of(
                        "name", " Notgroschen ",
                        "goalType", "emergency_fund",
                        "targetAmount", 5000,
                        "targetDate", "2027-06-01",
                        "monthlyContribution", 200));

        List<ProposedAction> result = validator.validateAndNormalize(List.of(input));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).payload().get("name")).isEqualTo("Notgroschen");
        assertThat(result.get(0).payload().get("goalType")).isEqualTo("EMERGENCY_FUND");
        assertThat(result.get(0).payload().get("targetAmount")).isEqualTo(new BigDecimal("5000"));
    }

    @Test
    void validateGoal_rejectsInvalidType() {
        ProposedAction input = new ProposedAction(
                ProposedActionType.CREATE_GOAL,
                "goal",
                Map.of("name", "Test", "goalType", "INVALID", "targetAmount", 100));

        assertThat(validator.validateAndNormalize(List.of(input))).isEmpty();
    }
}
