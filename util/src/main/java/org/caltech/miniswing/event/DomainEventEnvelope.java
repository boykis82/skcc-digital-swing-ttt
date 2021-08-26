package org.caltech.miniswing.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DomainEventEnvelope<T> {
    private T data;
    private String messageType;
    private LocalDateTime eventCreatedAt;

    public DomainEventEnvelope() {
        this.data = null;
        this.eventCreatedAt = LocalDateTime.now();
    }

    public DomainEventEnvelope(T data, String messageType) {
        this.data = data;
        this.messageType = messageType;
        this.eventCreatedAt = LocalDateTime.now();
    }
}
