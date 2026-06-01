package de.fiscalnorth.transaction.controller;

import de.fiscalnorth.transaction.dto.InsightsResponse;
import de.fiscalnorth.transaction.service.InsightsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final InsightsService insightsService;

    public InsightsController(InsightsService insightsService) {
        this.insightsService = insightsService;
    }

    @GetMapping
    public ResponseEntity<InsightsResponse> getInsights(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        int y = year > 0 ? year : LocalDate.now().getYear();
        int m = month > 0 ? month : LocalDate.now().getMonthValue();

        if (m > 0) {
            return ResponseEntity.ok(insightsService.getInsights(y, m));
        }
        return ResponseEntity.ok(insightsService.getInsightsForYear(y));
    }
}
