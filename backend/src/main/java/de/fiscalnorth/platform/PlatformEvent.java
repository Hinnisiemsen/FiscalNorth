package de.fiscalnorth.platform;

import java.time.Instant;

public record PlatformEvent(String type, String payload, Instant occurredAt) {
    public PlatformEvent(String type, String payload) {
        this(type, payload, Instant.now());
    }
}
