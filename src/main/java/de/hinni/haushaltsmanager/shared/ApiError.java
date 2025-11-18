package de.hinni.haushaltsmanager.shared;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int httpStatus,
        String error,
        String message,
        String path
) {}
