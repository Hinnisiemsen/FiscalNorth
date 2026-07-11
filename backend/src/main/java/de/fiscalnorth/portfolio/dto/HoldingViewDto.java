package de.fiscalnorth.portfolio.dto;

import de.fiscalnorth.portfolio.model.AssetClass;

import java.math.BigDecimal;

public record HoldingViewDto(
        Long id,
        String symbol,
        String name,
        BigDecimal quantity,
        BigDecimal costBasis,
        AssetClass assetClass,
        BigDecimal currentPrice,
        BigDecimal marketValue,
        BigDecimal unrealizedGain,
        String lastUpdatedBy,
        boolean priceStale) {}
