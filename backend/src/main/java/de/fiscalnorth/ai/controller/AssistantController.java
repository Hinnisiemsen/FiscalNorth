package de.fiscalnorth.ai.controller;

import de.fiscalnorth.ai.dto.AssistantContextDto;
import de.fiscalnorth.ai.dto.AssistantStatusDto;
import de.fiscalnorth.ai.dto.ChatRequest;
import de.fiscalnorth.ai.dto.ChatResponse;
import de.fiscalnorth.ai.service.AssistantService;
import de.fiscalnorth.auth.CurrentUserService;
import de.fiscalnorth.billing.model.PremiumFeature;
import de.fiscalnorth.billing.service.EntitlementService;
import de.fiscalnorth.shared.Messages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;
    private final Messages messages;
    private final CurrentUserService currentUserService;
    private final EntitlementService entitlementService;

    @GetMapping("/status")
    public ResponseEntity<AssistantStatusDto> status() {
        if (!assistantService.isAvailable()) {
            return ResponseEntity.ok(new AssistantStatusDto(
                    false, messages.get("assistant.status.unavailable"), false));
        }
        if (!entitlementService.hasFeature(
                currentUserService.getCurrentUser(), PremiumFeature.AI_ASSISTANT)) {
            return ResponseEntity.ok(new AssistantStatusDto(
                    false, messages.get("assistant.status.premiumRequired"), false));
        }
        if (assistantService.isDemo()) {
            return ResponseEntity.ok(new AssistantStatusDto(
                    true, messages.get("assistant.status.demo"), true));
        }
        return ResponseEntity.ok(new AssistantStatusDto(
                true, messages.get("assistant.status.ready"), false));
    }

    @GetMapping("/context")
    public ResponseEntity<AssistantContextDto> context() {
        return ResponseEntity.ok(new AssistantContextDto(assistantService.contextSnapshot()));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(assistantService.chat(request.message(), request.conversationId()));
    }
}
