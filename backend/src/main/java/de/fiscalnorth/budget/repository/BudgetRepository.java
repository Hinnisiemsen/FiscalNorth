package de.fiscalnorth.budget.repository;

import de.fiscalnorth.budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findAllByOwnerId(Long ownerId);

    Optional<Budget> findByIdAndOwnerId(Long id, Long ownerId);
}
