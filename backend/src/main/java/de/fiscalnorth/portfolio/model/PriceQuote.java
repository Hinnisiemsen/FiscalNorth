package de.fiscalnorth.portfolio.model;

import de.fiscalnorth.shared.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
public class PriceQuote extends BaseEntity {
    private String symbol;
    private BigDecimal price;
    private String currency;
    private Instant fetchedAt;
}
