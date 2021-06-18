package org.caltech.miniswing.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DomainEventEnvelope<T> {

    private T data;
    private LocalDateTime eventCreatedAt;

    public DomainEventEnvelope() {
        this.data = null;
        this.eventCreatedAt = LocalDateTime.now();
    }

    public DomainEventEnvelope(T data) {
        this.data = data;
        this.eventCreatedAt = LocalDateTime.now();
    }
}
