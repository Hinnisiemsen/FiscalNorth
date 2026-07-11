package de.fiscalnorth.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import de.fiscalnorth.portfolio.model.PriceQuote;
import de.fiscalnorth.portfolio.repository.PriceQuoteRepository;
import de.fiscalnorth.shared.LocalizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PriceQuoteService {

    private final PriceQuoteRepository priceQuoteRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.portfolio.price-api-key:}")
    private String apiKey;

    @Value("${app.portfolio.price-provider:manual-fallback}")
    private String priceProvider;

    public BigDecimal getLatestPrice(String symbol) {
        return priceQuoteRepository.findTopBySymbolOrderByFetchedAtDesc(symbol.toUpperCase())
                .filter(this::isFresh)
                .map(PriceQuote::getPrice)
                .orElseGet(() -> refreshPrice(symbol));
    }

    public BigDecimal refreshPrice(String symbol) {
        String normalized = symbol.toUpperCase();
        if (apiKey == null || apiKey.isBlank() || "manual-fallback".equalsIgnoreCase(priceProvider)) {
            return priceQuoteRepository.findTopBySymbolOrderByFetchedAtDesc(normalized)
                    .map(PriceQuote::getPrice)
                    .orElse(BigDecimal.ZERO);
        }
        try {
            JsonNode response = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("www.alphavantage.co")
                            .path("/query")
                            .queryParam("function", "GLOBAL_QUOTE")
                            .queryParam("symbol", normalized)
                            .queryParam("apikey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            if (response == null) {
                throw new LocalizedException("error.portfolio.priceUnavailable");
            }
            String priceText = response.path("Global Quote").path("05. price").asText("");
            if (priceText.isBlank()) {
                throw new LocalizedException("error.portfolio.priceUnavailable");
            }
            BigDecimal price = new BigDecimal(priceText);
            PriceQuote quote = new PriceQuote();
            quote.setSymbol(normalized);
            quote.setPrice(price);
            quote.setCurrency("USD");
            quote.setFetchedAt(Instant.now());
            priceQuoteRepository.save(quote);
            return price;
        } catch (Exception ex) {
            return priceQuoteRepository.findTopBySymbolOrderByFetchedAtDesc(normalized)
                    .map(PriceQuote::getPrice)
                    .orElse(BigDecimal.ZERO);
        }
    }

    private boolean isFresh(PriceQuote quote) {
        return quote.getFetchedAt().isAfter(Instant.now().minus(24, ChronoUnit.HOURS));
    }
}
