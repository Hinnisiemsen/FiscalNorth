package de.fiscalnorth.ai.controller;

import de.fiscalnorth.ai.dto.AssistantStatusDto;
import de.fiscalnorth.ai.dto.ChatRequest;
import de.fiscalnorth.ai.dto.ChatResponse;
import de.fiscalnorth.ai.service.AssistantService;
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

    @GetMapping("/status")
    public ResponseEntity<AssistantStatusDto> status() {
        boolean available = assistantService.isAvailable();
        String message = available
                ? "Fiscal North ist bereit, deine Fragen zu beantworten."
                : "Fiscal North ist vorübergehend nicht verfügbar. Bitte den Administrator kontaktieren.";
        return ResponseEntity.ok(new AssistantStatusDto(available, message));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(assistantService.chat(request.message(), request.conversationId()));
    }
}
