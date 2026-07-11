package de.fiscalnorth.budget.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.budget.dto.BudgetWithUsage;
import de.fiscalnorth.budget.dto.CreateBudgetRequest;
import de.fiscalnorth.budget.dto.MemberSpendingDto;
import de.fiscalnorth.budget.model.Budget;
import de.fiscalnorth.budget.repository.BudgetRepository;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import de.fiscalnorth.transaction.repository.TransactionSplitRepository;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final TransactionSplitRepository transactionSplitRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private final HouseholdScopeService householdScopeService;

    public List<Budget> getAllBudgets() {
        Long householdId = householdScopeService.requireHouseholdId();
        return budgetRepository.findAllByHouseholdId(householdId);
    }

    public List<BudgetWithUsage> getBudgetsWithUsage() {
        Long householdId = householdScopeService.requireHouseholdId();
        return budgetRepository.findAllByHouseholdId(householdId).stream()
                .map(budget -> toBudgetWithUsage(householdId, budget))
                .collect(Collectors.toList());
    }

    public List<BudgetWithUsage> getBudgetsWithUsageForOwner(Long ownerId) {
        return getBudgetsWithUsage();
    }

    private BudgetWithUsage toBudgetWithUsage(Long householdId, Budget budget) {
        BigDecimal spent = BigDecimal.ZERO;
        List<MemberSpendingDto> memberBreakdown = List.of();
        if (budget.getCategory() != null) {
            Long categoryId = budget.getCategory().getId();
            BigDecimal direct = paymentTransactionRepository.sumHouseholdExpenseByCategoryIdExcludingSplitParents(
                    householdId, categoryId, budget.getStartDate(), budget.getEndDate());
            BigDecimal fromSplits = transactionSplitRepository.sumHouseholdExpenseAmountByCategoryIdAndDateRange(
                    householdId, categoryId, budget.getStartDate(), budget.getEndDate());
            spent = direct.add(fromSplits);
            memberBreakdown = mapMemberBreakdown(paymentTransactionRepository.sumHouseholdExpenseByMemberAndCategory(
                    householdId, categoryId, budget.getStartDate(), budget.getEndDate()));
        }
        BigDecimal remaining = budget.getLimit().subtract(spent);
        return new BudgetWithUsage(
                budget.getId(),
                budget.getName(),
                budget.getLimit(),
                budget.getStartDate(),
                budget.getEndDate(),
                spent,
                remaining,
                budget.getCategory() != null ? budget.getCategory().getId() : null,
                budget.getCategory() != null ? budget.getCategory().getName() : null,
                memberBreakdown);
    }

    private List<MemberSpendingDto> mapMemberBreakdown(List<Object[]> rows) {
        List<MemberSpendingDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            String name = row[0] != null ? row[0].toString() : "Member";
            BigDecimal amount = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            result.add(new MemberSpendingDto(name, amount));
        }
        return result;
    }

    public Budget getBudgetById(Long id) {
        Long householdId = householdScopeService.requireHouseholdId();
        return budgetRepository.findByIdAndHouseholdId(id, householdId)
                .orElseThrow(() -> new RessourceNotFoundException("Budget", "id", id));
    }

    @Transactional
    public Budget createBudget(CreateBudgetRequest request) {
        User owner = currentUserService.getCurrentUser();
        Household household = householdScopeService.requireHousehold();
        Budget budget = new Budget();
        budget.setName(request.name());
        budget.setLimit(request.limit());
        budget.setStartDate(request.startDate());
        budget.setEndDate(request.endDate());
        budget.setOwner(owner);
        budget.setHousehold(household);
        if (request.categoryId() != null) {
            Category category = categoryRepository.findByIdAndHouseholdId(request.categoryId(), household.getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Category", "id", request.categoryId()));
            budget.setCategory(category);
        }
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        Budget budget = getBudgetById(id);
        budgetRepository.delete(budget);
    }
}
