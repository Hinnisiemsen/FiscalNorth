package de.fiscalnorth.goal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fiscalnorth.ai.client.GeminiClient;
import de.fiscalnorth.ai.config.AiProperties;
import de.fiscalnorth.ai.service.FinancialContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalPlanningServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private FinancialContextService financialContextService;

    @Mock
    private AiProperties aiProperties;

    private GoalPlanningService service;

    @BeforeEach
    void setUp() {
        service = new GoalPlanningService(
                geminiClient,
                financialContextService,
                aiProperties,
                new ObjectMapper(),
                org.mockito.Mockito.mock(de.fiscalnorth.auth.CurrentUserService.class),
                org.mockito.Mockito.mock(de.fiscalnorth.billing.service.EntitlementService.class));
    }

    @Test
    void generatePlan_fallbackWhenAiDisabled() {
        when(aiProperties.enabled()).thenReturn(false);

        var plan = service.generatePlan(Map.of(
                "priorities", List.of("EMERGENCY_FUND"),
                "monthlyWillingToSave", 250));

        assertThat(plan.recommendedGoals()).isNotEmpty();
        assertThat(plan.recommendedGoals().get(0).goalType().name()).isEqualTo("EMERGENCY_FUND");
        assertThat(plan.monthlySavingsTarget()).isEqualByComparingTo(new BigDecimal("250"));
    }

    @Test
    void generatePlan_parsesAiJson() {
        when(aiProperties.enabled()).thenReturn(true);
        ReflectionTestUtils.setField(service, "apiKey", "test-key");
        when(financialContextService.buildContextSnapshot()).thenReturn("context");
        when(geminiClient.generate(anyString(), anyString())).thenReturn("""
                {
                  "summary": "Dein Plan",
                  "recommendedGoals": [
                    {
                      "name": "Notgroschen",
                      "goalType": "EMERGENCY_FUND",
                      "targetAmount": 5000,
                      "targetDate": "2027-01-01",
                      "monthlyContribution": 200,
                      "rationale": "Sicherheit zuerst"
                    }
                  ],
                  "monthlySavingsTarget": 200,
                  "insights": ["Tipp"]
                }
                """);

        var plan = service.generatePlan(Map.of());

        assertThat(plan.summary()).isEqualTo("Dein Plan");
        assertThat(plan.recommendedGoals()).hasSize(1);
        assertThat(plan.recommendedGoals().get(0).name()).isEqualTo("Notgroschen");
        assertThat(plan.insights()).containsExactly("Tipp");
    }
}
