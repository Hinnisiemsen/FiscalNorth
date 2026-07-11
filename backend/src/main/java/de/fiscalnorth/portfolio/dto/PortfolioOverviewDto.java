package de.fiscalnorth.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioOverviewDto(
        Long id,
        String name,
        BigDecimal totalValue,
        BigDecimal totalCost,
        BigDecimal unrealizedGain,
        List<HoldingViewDto> holdings) {}
