package de.fiscalnorth.portfolio.repository;

import de.fiscalnorth.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findAllByHouseholdId(Long householdId);

    Optional<Portfolio> findByIdAndHouseholdId(Long id, Long householdId);
}
