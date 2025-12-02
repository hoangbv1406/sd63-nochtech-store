package com.project.shopapp.shared.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class DomainEvent extends ApplicationEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;

    public DomainEvent(Object source) {
        super(source);
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
}
