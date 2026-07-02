package de.fiscalnorth.goal.repository;

import de.fiscalnorth.goal.model.FinancialGoal;
import de.fiscalnorth.goal.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    List<FinancialGoal> findAllByOwnerId(Long ownerId);

    List<FinancialGoal> findAllByOwnerIdAndStatus(Long ownerId, GoalStatus status);

    Optional<FinancialGoal> findByIdAndOwnerId(Long id, Long ownerId);
}
