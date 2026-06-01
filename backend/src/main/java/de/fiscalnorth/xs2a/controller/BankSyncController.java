package de.fiscalnorth.xs2a.controller;

import de.fiscalnorth.xs2a.dto.BankConsentDto;
import de.fiscalnorth.xs2a.dto.BankSyncStatusDto;
import de.fiscalnorth.xs2a.dto.CreateConsentResponseDto;
import de.fiscalnorth.xs2a.service.BankSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API for XS2A bank sync (Berlin Group / finAPI).
 * Endpoints return unavailable when app.xs2a.enabled is false or credentials are not set.
 */
@RestController
@RequestMapping("/api/bank-sync")
public class BankSyncController {

    private final BankSyncService bankSyncService;

    public BankSyncController(BankSyncService bankSyncService) {
        this.bankSyncService = bankSyncService;
    }

    @GetMapping("/status")
    public ResponseEntity<BankSyncStatusDto> getStatus() {
        return ResponseEntity.ok(bankSyncService.getStatus());
    }

    @PostMapping("/consent")
    public ResponseEntity<CreateConsentResponseDto> createConsent() {
        return ResponseEntity.ok(bankSyncService.createConsent());
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> handleCallback(@RequestParam(required = false) String consentId) {
        String redirectPath = bankSyncService.handleCallback(consentId != null ? consentId : "");
        return ResponseEntity.ok(Map.of("redirectTo", redirectPath));
    }

    @PostMapping("/sync")
    public ResponseEntity<BankSyncService.SyncResultDto> sync(@RequestBody(required = false) Map<String, String> body) {
        String consentId = body != null ? body.get("consentId") : null;
        if (consentId == null || consentId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bankSyncService.sync(consentId));
    }

    @GetMapping("/consents")
    public ResponseEntity<List<BankConsentDto>> getConsents() {
        return ResponseEntity.ok(bankSyncService.getConsents());
    }
}
