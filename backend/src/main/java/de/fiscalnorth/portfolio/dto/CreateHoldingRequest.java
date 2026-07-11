package de.fiscalnorth.portfolio.dto;

import de.fiscalnorth.portfolio.model.AssetClass;

import java.math.BigDecimal;

public record CreateHoldingRequest(
        String symbol,
        String name,
        BigDecimal quantity,
        BigDecimal costBasis,
        AssetClass assetClass) {}
