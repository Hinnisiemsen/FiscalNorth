package de.fiscalnorth.budget.service;

import de.fiscalnorth.budget.dto.CreateBudgetRequest;
import de.fiscalnorth.budget.model.Budget;
import de.fiscalnorth.budget.repository.BudgetRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
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
