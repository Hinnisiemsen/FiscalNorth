package de.hinni.haushaltsmanager.budget.repository;

import de.hinni.haushaltsmanager.budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
