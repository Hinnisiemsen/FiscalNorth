package de.fiscalnorth.portfolio.controller;

import de.fiscalnorth.portfolio.dto.CreateHoldingRequest;
import de.fiscalnorth.portfolio.dto.PortfolioOverviewDto;
import de.fiscalnorth.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<PortfolioOverviewDto> getPortfolio() {
        return ResponseEntity.ok(portfolioService.getHouseholdPortfolio());
    }

    @PostMapping("/holdings")
    public ResponseEntity<PortfolioOverviewDto> addHolding(@RequestBody @Valid CreateHoldingRequest request) {
        return ResponseEntity.ok(portfolioService.addHolding(request));
    }
}
