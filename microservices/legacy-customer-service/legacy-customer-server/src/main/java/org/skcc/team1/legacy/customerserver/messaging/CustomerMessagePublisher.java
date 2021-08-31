package org.skcc.team1.legacy.customerserver.messaging;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerMessagePublisher {

    private final MessageSender messageSender;
    private final String customerEventQueueName;

    private static final String MESSAGE_TYPE_CUSTOMER_CREATED = "CustomerCreated";
    private static final String MESSAGE_TYPE_CUSTOMER_UPDATED = "CustomerUpdated";
    private static final String MESSAGE_TYPE_CUSTOMER_DELETED = "CustomerDeleted";

    @Autowired
    public CustomerMessagePublisher(MessageSender messageSender, String customerEventQueueName) {
        this.messageSender = messageSender;
        this.customerEventQueueName = customerEventQueueName;
    }

    public void sendCustomerCreatedEvent(CustResponseDto custResponseDto) {
        messageSender.sendMessage(
                customerEventQueueName,
                new DomainEventEnvelope<>(custResponseDto, MESSAGE_TYPE_CUSTOMER_CREATED)
        );
    }
}
