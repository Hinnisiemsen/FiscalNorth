package de.fiscalnorth.shared;

import de.fiscalnorth.ai.AiDisabledException;
import de.fiscalnorth.auth.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Messages messages;

    public GlobalExceptionHandler(Messages messages) {
        this.messages = messages;
    }

    @ExceptionHandler(RessourceNotFoundException.class)
    public ResponseEntity<ApiError> handleRessourceNotFound(
            RessourceNotFoundException exception,
            ServerWebExchange exchange
    ) {
        String message = messages.get(
                "error.notFound",
                exception.getResourceName(),
                exception.getFieldName(),
                exception.getFieldValue());
        return apiError(HttpStatus.NOT_FOUND, message, exchange);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
            UnauthorizedException ex,
            ServerWebExchange exchange
    ) {
        return apiError(HttpStatus.UNAUTHORIZED, messages.get("error.unauthorized"), exchange);
    }

    @ExceptionHandler(AiDisabledException.class)
    public ResponseEntity<ApiError> handleAiDisabled(
            AiDisabledException ex,
            ServerWebExchange exchange
    ) {
        return apiError(HttpStatus.SERVICE_UNAVAILABLE, messages.get("error.ai.disabled"), exchange);
    }

    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ApiError> handleLocalized(
            LocalizedException ex,
            ServerWebExchange exchange
    ) {
        HttpStatus status = exchange.getRequest().getPath().value().contains("/assistant")
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.CONFLICT;
        return apiError(status, messages.get(ex.getMessageCode(), ex.getMessageArgs()), exchange);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex,
            ServerWebExchange exchange
    ) {
        HttpStatus status = exchange.getRequest().getPath().value().contains("/assistant")
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.CONFLICT;
        return apiError(status, ex.getMessage(), exchange);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            ServerWebExchange exchange
    ) {
        return apiError(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            ServerWebExchange exchange
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> {
                    String code = error.getDefaultMessage();
                    if (code != null && code.startsWith("{") && code.endsWith("}")) {
                        return messages.get(code.substring(1, code.length() - 1));
                    }
                    return error.getDefaultMessage() != null ? error.getDefaultMessage() : messages.get("error.unexpected");
                })
                .orElse(messages.get("error.unexpected"));
        return apiError(HttpStatus.BAD_REQUEST, message, exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(
            Exception ex,
            ServerWebExchange exchange
    ) {
        log.error("Unexpected error at {}: {}", exchange.getRequest().getPath(), ex.getMessage(), ex);
        return apiError(HttpStatus.INTERNAL_SERVER_ERROR, messages.get("error.unexpected"), exchange);
    }

    private ResponseEntity<ApiError> apiError(HttpStatus status, String message, ServerWebExchange exchange) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value()
        );
        return new ResponseEntity<>(apiError, status);
    }
}
