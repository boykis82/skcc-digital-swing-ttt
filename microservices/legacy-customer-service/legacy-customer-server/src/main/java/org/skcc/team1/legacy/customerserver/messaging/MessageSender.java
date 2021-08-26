package org.skcc.team1.legacy.customerserver.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.caltech.miniswing.event.DomainEventEnvelope;

public interface MessageSender {
     void sendMessage(String queueName, DomainEventEnvelope<?> domainEventEnvelope);
}
