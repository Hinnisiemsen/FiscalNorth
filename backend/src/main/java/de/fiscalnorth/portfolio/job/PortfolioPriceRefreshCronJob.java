package de.fiscalnorth.portfolio.job;

import de.fiscalnorth.portfolio.repository.HoldingRepository;
import de.fiscalnorth.portfolio.service.PriceQuoteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioPriceRefreshCronJob {

    private static final Logger log = LoggerFactory.getLogger(PortfolioPriceRefreshCronJob.class);

    private final HoldingRepository holdingRepository;
    private final PriceQuoteService priceQuoteService;

    @Value("${app.portfolio.cron.price-refresh-enabled:true}")
    private boolean enabled;

    @Scheduled(cron = "${app.portfolio.cron.price-refresh:0 0 6 * * *}")
    public void refreshStalePrices() {
        if (!enabled) {
            return;
        }
        var symbols = holdingRepository.findDistinctSymbols();
        if (symbols.isEmpty()) {
            return;
        }
        int refreshed = 0;
        for (String symbol : symbols) {
            try {
                priceQuoteService.refreshPrice(symbol);
                refreshed++;
            } catch (Exception ex) {
                log.debug("Price refresh skipped for {}: {}", symbol, ex.getMessage());
            }
        }
        log.info("Portfolio price cron refreshed {} symbol(s)", refreshed);
    }
}
