package de.fiscalnorth.portfolio.model;

import de.fiscalnorth.shared.BaseEntity;
import de.fiscalnorth.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Holding extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    private String symbol;
    private String name;

    private BigDecimal quantity;
    private BigDecimal costBasis;

    @Enumerated(EnumType.STRING)
    private AssetClass assetClass = AssetClass.STOCK;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by_id")
    private User lastUpdatedBy;
}
