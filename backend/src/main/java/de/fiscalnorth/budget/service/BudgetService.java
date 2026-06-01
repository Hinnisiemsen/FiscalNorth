package de.fiscalnorth.budget.service;

import de.fiscalnorth.budget.dto.BudgetWithUsage;
import de.fiscalnorth.budget.dto.CreateBudgetRequest;
import de.fiscalnorth.budget.model.Budget;
import de.fiscalnorth.budget.repository.BudgetRepository;
import de.fiscalnorth.category.model.Category;
import de.fiscalnorth.category.repository.CategoryRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.transaction.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CategoryRepository categoryRepository;

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public List<BudgetWithUsage> getBudgetsWithUsage() {
        return budgetRepository.findAll().stream()
                .map(budget -> {
                    BigDecimal spent;
                    if (budget.getCategory() != null) {
                        spent = paymentTransactionRepository.sumExpenseAmountByCategoryIdAndDateRange(
                                budget.getCategory().getId(),
                                budget.getStartDate(),
                                budget.getEndDate());
                    } else {
                        spent = paymentTransactionRepository.sumExpenseAmountByCategoryNameAndDateRange(
                                budget.getName(),
                                budget.getStartDate(),
                                budget.getEndDate());
                    }
                    return new BudgetWithUsage(
                            budget.getId(),
                            budget.getName(),
                            budget.getLimit(),
                            budget.getStartDate(),
                            budget.getEndDate(),
                            spent,
                            budget.getCategory() != null ? budget.getCategory().getId() : null,
                            budget.getCategory() != null ? budget.getCategory().getName() : null);
                })
                .collect(Collectors.toList());
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Budget", "id", id));
    }

    @Transactional
    public Budget createBudget(CreateBudgetRequest request) {
        Budget budget = new Budget();
        budget.setName(request.name());
        budget.setLimit(request.limit());
        budget.setStartDate(request.startDate());
        budget.setEndDate(request.endDate());
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RessourceNotFoundException("Category", "id", request.categoryId()));
            budget.setCategory(category);
        }
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RessourceNotFoundException("Budget", "id", id);
        }
        budgetRepository.deleteById(id);
    }
}
