package de.fiscalnorth.goal.service;

import de.fiscalnorth.account.model.DepositAccount;
import de.fiscalnorth.account.repository.DepositAccountRepository;
import de.fiscalnorth.contract.repository.ContractRepository;
import de.fiscalnorth.goal.dto.GoalWithProgress;
import de.fiscalnorth.goal.model.FinancialGoal;
import de.fiscalnorth.goal.model.GoalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalProgressService {

    private final DepositAccountRepository depositAccountRepository;
    private final ContractRepository contractRepository;

    public GoalWithProgress toGoalWithProgress(FinancialGoal goal) {
        BigDecimal progressAmount = resolveProgressAmount(goal);
        BigDecimal targetAmount = goal.getTargetAmount() != null ? goal.getTargetAmount() : BigDecimal.ZERO;
        BigDecimal progressPercent = computeProgressPercent(progressAmount, targetAmount);
        BigDecimal remainingAmount = targetAmount.subtract(progressAmount).max(BigDecimal.ZERO);
        Long daysRemaining = goal.getTargetDate() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), goal.getTargetDate())
                : null;
        boolean onTrack = computeOnTrack(goal, progressAmount, targetAmount, daysRemaining);

        String linkedAccountName = null;
        if (goal.getLinkedAccountId() != null) {
            linkedAccountName = depositAccountRepository
                    .findByIdAndOwnerId(goal.getLinkedAccountId(), goal.getOwner().getId())
                    .map(DepositAccount::getName)
                    .orElse(null);
        }

        GoalStatus status = goal.getStatus();
        if (status == GoalStatus.ACTIVE && progressPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            status = GoalStatus.COMPLETED;
        }

        return new GoalWithProgress(
                goal.getId(),
                goal.getName(),
                goal.getGoalType(),
                targetAmount,
                goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO,
                goal.getTargetDate(),
                goal.getLinkedAccountId(),
                linkedAccountName,
                goal.getMonthlyContribution(),
                status,
                progressAmount,
                progressPercent,
                remainingAmount,
                daysRemaining,
                onTrack);
    }

    private BigDecimal resolveProgressAmount(FinancialGoal goal) {
        if (goal.getLinkedAccountId() != null) {
            Optional<DepositAccount> account = depositAccountRepository
                    .findByIdAndOwnerId(goal.getLinkedAccountId(), goal.getOwner().getId());
            if (account.isPresent()) {
                return account.get().getBalance();
            }
        }
        return goal.getCurrentAmount() != null ? goal.getCurrentAmount() : BigDecimal.ZERO;
    }

    public BigDecimal computeProgressPercent(BigDecimal progressAmount, BigDecimal targetAmount) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal percent = progressAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, RoundingMode.HALF_UP);
        return percent.min(BigDecimal.valueOf(100));
    }

    private boolean computeOnTrack(
            FinancialGoal goal,
            BigDecimal progressAmount,
            BigDecimal targetAmount,
            Long daysRemaining) {
        if (goal.getTargetDate() == null) {
            return true;
        }
        if (daysRemaining != null && daysRemaining <= 0) {
            return progressAmount.compareTo(targetAmount) >= 0;
        }

        BigDecimal remaining = targetAmount.subtract(progressAmount).max(BigDecimal.ZERO);
        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        long monthsRemaining = Math.max(1, ChronoUnit.MONTHS.between(LocalDate.now(), goal.getTargetDate()));
        BigDecimal requiredMonthlyPace = remaining.divide(
                BigDecimal.valueOf(monthsRemaining), 2, RoundingMode.HALF_UP);

        BigDecimal plannedContribution = goal.getMonthlyContribution() != null
                ? goal.getMonthlyContribution()
                : BigDecimal.ZERO;

        return plannedContribution.compareTo(requiredMonthlyPace) >= 0;
    }
}
