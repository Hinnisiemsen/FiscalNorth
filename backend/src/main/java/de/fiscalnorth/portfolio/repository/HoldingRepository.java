package de.fiscalnorth.portfolio.repository;

import de.fiscalnorth.portfolio.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findAllByPortfolioId(Long portfolioId);

    @Query("SELECT DISTINCT h.symbol FROM Holding h")
    List<String> findDistinctSymbols();
}
