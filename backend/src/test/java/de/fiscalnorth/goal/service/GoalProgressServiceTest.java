package de.fiscalnorth.goal.service;

import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.goal.model.FinancialGoal;
import de.fiscalnorth.goal.model.GoalStatus;
import de.fiscalnorth.goal.model.GoalType;
import de.fiscalnorth.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalProgressServiceTest {

    @Mock
    private DepositAccountRepository depositAccountRepository;

    @Mock
    private ContractRepository contractRepository;

    private GoalProgressService service;

    @BeforeEach
    void setUp() {
        service = new GoalProgressService(depositAccountRepository, contractRepository);
    }

    @Test
    void computeProgressPercent_capsAt100() {
        assertThat(service.computeProgressPercent(new BigDecimal("6000"), new BigDecimal("5000")))
                .isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    void toGoalWithProgress_usesLinkedAccountBalance() {
        User owner = new User();
        owner.setId(1L);

        FinancialGoal goal = new FinancialGoal();
        goal.setId(10L);
        goal.setName("Notgroschen");
        goal.setGoalType(GoalType.EMERGENCY_FUND);
        goal.setTargetAmount(new BigDecimal("10000"));
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setLinkedAccountId(2L);
        goal.setMonthlyContribution(new BigDecimal("300"));
        goal.setTargetDate(LocalDate.now().plusMonths(12));
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setOwner(owner);

        DepositAccount account = new DepositAccount();
        account.setId(2L);
        account.setName("Notgroschen");
        account.setBalance(new BigDecimal("8500"));

        when(depositAccountRepository.findByIdAndOwnerId(2L, 1L)).thenReturn(Optional.of(account));

        var progress = service.toGoalWithProgress(goal);

        assertThat(progress.progressAmount()).isEqualByComparingTo("8500");
        assertThat(progress.progressPercent()).isEqualByComparingTo("85.00");
        assertThat(progress.remainingAmount()).isEqualByComparingTo("1500");
        assertThat(progress.linkedAccountName()).isEqualTo("Notgroschen");
    }

    @Test
    void toGoalWithProgress_fallsBackToManualCurrentAmount() {
        User owner = new User();
        owner.setId(1L);

        FinancialGoal goal = new FinancialGoal();
        goal.setId(11L);
        goal.setName("Urlaub");
        goal.setGoalType(GoalType.VACATION);
        goal.setTargetAmount(new BigDecimal("3000"));
        goal.setCurrentAmount(new BigDecimal("1200"));
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setOwner(owner);

        var progress = service.toGoalWithProgress(goal);

        assertThat(progress.progressAmount()).isEqualByComparingTo("1200");
        assertThat(progress.progressPercent()).isEqualByComparingTo("40.00");
    }
}
