package de.fiscalnorth.ai.job;

import de.fiscalnorth.ai.config.AiCronProperties;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.service.EntitlementService;
import de.fiscalnorth.budget.dto.BudgetWithUsage;
import de.fiscalnorth.budget.service.BudgetService;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.model.HouseholdMember;
import de.fiscalnorth.household.repository.HouseholdMemberRepository;
import de.fiscalnorth.household.repository.HouseholdRepository;
import de.fiscalnorth.notification.dto.NotificationDto;
import de.fiscalnorth.notification.model.NotificationSeverity;
import de.fiscalnorth.notification.model.NotificationType;
import de.fiscalnorth.notification.service.NotificationService;
import de.fiscalnorth.support.TestMessages;
import de.fiscalnorth.user.model.AuthProvider;
import de.fiscalnorth.user.model.User;
import de.fiscalnorth.user.model.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetAlertCronJobTest {

    @Mock
    private BudgetService budgetService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HouseholdRepository householdRepository;

    @Mock
    private HouseholdMemberRepository householdMemberRepository;

    @Mock
    private EntitlementService entitlementService;

    private BudgetAlertCronJob budgetAlertCronJob;

    @BeforeEach
    void setUp() {
        System.setProperty("fiscalnorth.default-locale", "en");
        AiCronProperties cronProperties = new AiCronProperties(true, null, null, null, null, 30);
        budgetAlertCronJob = new BudgetAlertCronJob(
                cronProperties,
                budgetService,
                notificationService,
                TestMessages.create(),
                householdRepository,
                householdMemberRepository,
                entitlementService);
    }

    @AfterEach
    void clearLocaleProperty() {
        System.clearProperty("fiscalnorth.default-locale");
    }

    @Test
    void scanBudgets_createsWarningForEachPremiumHouseholdMember() {
        User alex = user(1L, "Alex");
        User jamie = user(2L, "Jamie");
        Household household = new Household();
        household.setId(10L);
        household.setName("Test Household");

        HouseholdMember alexMember = new HouseholdMember();
        alexMember.setUser(alex);
        alexMember.setHousehold(household);
        HouseholdMember jamieMember = new HouseholdMember();
        jamieMember.setUser(jamie);
        jamieMember.setHousehold(household);

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        when(householdRepository.findAll()).thenReturn(List.of(household));
        when(householdMemberRepository.findAllByHouseholdId(10L)).thenReturn(List.of(alexMember, jamieMember));
        when(entitlementService.hasFeature(alex, PremiumFeature.AI_NOTIFICATIONS)).thenReturn(true);
        when(entitlementService.hasFeature(jamie, PremiumFeature.AI_NOTIFICATIONS)).thenReturn(true);
        when(budgetService.getBudgetsWithUsageForHousehold(10L)).thenReturn(List.of(
                new BudgetWithUsage(
                        1L,
                        "Lebensmittel",
                        new BigDecimal("100.00"),
                        start,
                        end,
                        new BigDecimal("85.00"),
                        new BigDecimal("15.00"),
                        1L,
                        "Food",
                        List.of())));
        when(notificationService.createIfAbsent(
                        anyLong(),
                        anyString(),
                        anyString(),
                        anyString(),
                        eq(NotificationType.BUDGET_ALERT),
                        eq(NotificationSeverity.WARNING),
                        eq("budget-alert-cron")))
                .thenReturn(Optional.of(new NotificationDto(
                        1L,
                        "Budget nearly exhausted",
                        "msg",
                        NotificationType.BUDGET_ALERT,
                        NotificationSeverity.WARNING,
                        false,
                        "budget-alert-cron",
                        LocalDateTime.now())));

        budgetAlertCronJob.scanBudgets();

        verify(notificationService).createIfAbsent(
                eq(1L),
                contains("budget-alert:1:warning"),
                eq("Budget nearly exhausted"),
                anyString(),
                eq(NotificationType.BUDGET_ALERT),
                eq(NotificationSeverity.WARNING),
                eq("budget-alert-cron"));
        verify(notificationService).createIfAbsent(
                eq(2L),
                contains("budget-alert:1:warning"),
                eq("Budget nearly exhausted"),
                anyString(),
                eq(NotificationType.BUDGET_ALERT),
                eq(NotificationSeverity.WARNING),
                eq("budget-alert-cron"));
    }

    private static User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(name.toLowerCase() + "@example.com");
        user.setUserName(name);
        user.setUserRole(UserRole.User);
        user.setAuthProvider(AuthProvider.LOCAL);
        return user;
    }
}
