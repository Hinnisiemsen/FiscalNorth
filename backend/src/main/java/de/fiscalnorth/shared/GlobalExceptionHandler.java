package de.fiscalnorth.shared;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import de.fiscalnorth.ai.AiDisabledException;
import de.fiscalnorth.auth.UnauthorizedException;
import de.fiscalnorth.billing.BillingUnavailableException;
import de.fiscalnorth.billing.PremiumRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
            HttpServletRequest request
    ) {
        String message = messages.get(
                "error.notFound",
                exception.getResourceName(),
                exception.getFieldName(),
                exception.getFieldValue());
        return apiError(HttpStatus.NOT_FOUND, message, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        return apiError(HttpStatus.UNAUTHORIZED, messages.get("error.unauthorized"), request);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return apiError(HttpStatus.UNAUTHORIZED, messages.get("error.unauthorized"), request);
    }

    @ExceptionHandler(AiDisabledException.class)
    public ResponseEntity<ApiError> handleAiDisabled(
            AiDisabledException ex,
            HttpServletRequest request
    ) {
        return apiError(HttpStatus.SERVICE_UNAVAILABLE, messages.get("error.ai.disabled"), request);
    }

    @ExceptionHandler(PremiumRequiredException.class)
    public ResponseEntity<ApiError> handlePremiumRequired(
            PremiumRequiredException ex,
            HttpServletRequest request
    ) {
        return apiError(HttpStatus.FORBIDDEN, messages.get("error.premium.required"), request);
    }

    @ExceptionHandler(BillingUnavailableException.class)
    public ResponseEntity<ApiError> handleBillingUnavailable(
            BillingUnavailableException ex,
            HttpServletRequest request
    ) {
        return apiError(HttpStatus.SERVICE_UNAVAILABLE, messages.get("error.billing.unavailable"), request);
    }

    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<ApiError> handleLocalized(
            LocalizedException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = request.getRequestURI().contains("/assistant")
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.CONFLICT;
        return apiError(status, messages.get(ex.getMessageCode(), ex.getMessageArgs()), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = request.getRequestURI().contains("/assistant")
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.CONFLICT;
        return apiError(status, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return apiError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
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
        return apiError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return apiError(HttpStatus.INTERNAL_SERVER_ERROR, messages.get("error.unexpected"), request);
    }

    private ResponseEntity<ApiError> apiError(HttpStatus status, String message, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, status);
    }
}
