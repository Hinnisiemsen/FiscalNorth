package de.fiscalnorth.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import de.fiscalnorth.shared.LocalizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class GeminiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash}")
    private String model;

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    public GeminiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String generate(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new LocalizedException("error.gemini.apiKeyNotConfigured");
        }

        ObjectNode body = objectMapper.createObjectNode();
        ObjectNode systemInstruction = objectMapper.createObjectNode();
        ArrayNode systemParts = objectMapper.createArrayNode();
        systemParts.addObject().put("text", systemPrompt);
        systemInstruction.set("parts", systemParts);
        body.set("systemInstruction", systemInstruction);

        ArrayNode contents = objectMapper.createArrayNode();
        ObjectNode userContent = objectMapper.createObjectNode();
        ArrayNode userParts = objectMapper.createArrayNode();
        userParts.addObject().put("text", userMessage);
        userContent.set("parts", userParts);
        userContent.put("role", "user");
        contents.add(userContent);
        body.set("contents", contents);

        JsonNode response;
        try {
            response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new IllegalStateException(
                    "Gemini API " + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString(), ex);
        }

        if (response == null) {
            throw new LocalizedException("error.gemini.emptyResponse");
        }
        JsonNode candidates = response.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new LocalizedException("error.gemini.noCandidates", response);
        }
        return candidates.get(0).path("content").path("parts").get(0).path("text").asText("");
    }
}
