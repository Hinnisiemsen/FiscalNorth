package de.fiscalnorth.platform;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Lightweight event hook for future Kafka/RabbitMQ integration.
 * Domain services can publish {@link PlatformEvent} instances without messaging infrastructure today.
 */
@Component
public class PlatformEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PlatformEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(PlatformEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
