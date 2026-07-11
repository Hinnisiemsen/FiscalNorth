package de.fiscalnorth.portfolio.service;

import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.household.model.Household;
import de.fiscalnorth.household.service.HouseholdScopeService;
import de.fiscalnorth.portfolio.dto.CreateHoldingRequest;
import de.fiscalnorth.portfolio.dto.HoldingViewDto;
import de.fiscalnorth.portfolio.dto.PortfolioOverviewDto;
import de.fiscalnorth.portfolio.model.Holding;
import de.fiscalnorth.portfolio.model.Portfolio;
import de.fiscalnorth.portfolio.repository.HoldingRepository;
import de.fiscalnorth.portfolio.repository.PortfolioRepository;
import de.fiscalnorth.shared.RessourceNotFoundException;
import de.fiscalnorth.shared.SupportedCurrency;
import de.fiscalnorth.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final PriceQuoteService priceQuoteService;
    private final HouseholdScopeService householdScopeService;
    private final CurrentUserService currentUserService;

    public PortfolioOverviewDto getHouseholdPortfolio() {
        Household household = householdScopeService.requireHousehold();
        Portfolio portfolio = getOrCreatePortfolioInternal(household);
        return toOverview(portfolio);
    }

    @Transactional
    public PortfolioOverviewDto addHolding(CreateHoldingRequest request) {
        Portfolio portfolio = getOrCreatePortfolio();
        User user = currentUserService.getCurrentUser();
        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setSymbol(request.symbol().toUpperCase());
        holding.setName(request.name());
        holding.setQuantity(request.quantity());
        holding.setCostBasis(request.costBasis());
        holding.setAssetClass(request.assetClass());
        holding.setLastUpdatedBy(user);
        holdingRepository.save(holding);
        priceQuoteService.refreshPrice(holding.getSymbol());
        return toOverview(portfolio);
    }

    @Transactional
    Portfolio getOrCreatePortfolioInternal(Household household) {
        return portfolioRepository.findAllByHouseholdId(household.getId()).stream()
                .findFirst()
                .orElseGet(() -> createDefaultPortfolio(household));
    }

    private Portfolio getOrCreatePortfolio() {
        return getOrCreatePortfolioInternal(householdScopeService.requireHousehold());
    }

    private Portfolio createDefaultPortfolio(Household household) {
        Portfolio portfolio = new Portfolio();
        portfolio.setName(household.getName() + " Portfolio");
        portfolio.setBaseCurrency(SupportedCurrency.EURO);
        portfolio.setHousehold(household);
        return portfolioRepository.save(portfolio);
    }

    private PortfolioOverviewDto toOverview(Portfolio portfolio) {
        List<Holding> holdings = holdingRepository.findAllByPortfolioId(portfolio.getId());
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        List<HoldingViewDto> holdingViews = holdings.stream().map(h -> {
            BigDecimal price = priceQuoteService.getLatestPrice(h.getSymbol());
            BigDecimal marketValue = price.multiply(h.getQuantity());
            BigDecimal cost = h.getCostBasis() != null ? h.getCostBasis() : BigDecimal.ZERO;
            return new HoldingViewDto(
                    h.getId(),
                    h.getSymbol(),
                    h.getName(),
                    h.getQuantity(),
                    cost,
                    h.getAssetClass(),
                    price,
                    marketValue,
                    marketValue.subtract(cost),
                    h.getLastUpdatedBy() != null ? h.getLastUpdatedBy().getUserName() : null,
                    price.compareTo(BigDecimal.ZERO) == 0);
        }).toList();
        for (HoldingViewDto view : holdingViews) {
            totalValue = totalValue.add(view.marketValue());
            totalCost = totalCost.add(view.costBasis());
        }
        return new PortfolioOverviewDto(
                portfolio.getId(),
                portfolio.getName(),
                totalValue,
                totalCost,
                totalValue.subtract(totalCost),
                holdingViews);
    }

    public PortfolioOverviewDto getPortfolioById(Long id) {
        Long householdId = householdScopeService.requireHouseholdId();
        Portfolio portfolio = portfolioRepository.findByIdAndHouseholdId(id, householdId)
                .orElseThrow(() -> new RessourceNotFoundException("Portfolio", "id", id));
        return toOverview(portfolio);
    }
}
