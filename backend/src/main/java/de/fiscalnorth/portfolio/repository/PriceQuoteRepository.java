package de.fiscalnorth.portfolio.repository;

import de.fiscalnorth.portfolio.model.PriceQuote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceQuoteRepository extends JpaRepository<PriceQuote, Long> {
    Optional<PriceQuote> findTopBySymbolOrderByFetchedAtDesc(String symbol);
}
