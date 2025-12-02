package com.project.shopapp.shared.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private String correlationId;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
