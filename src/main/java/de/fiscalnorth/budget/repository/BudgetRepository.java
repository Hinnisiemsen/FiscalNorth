package de.fiscalnorth.budget.repository;

import de.fiscalnorth.budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
