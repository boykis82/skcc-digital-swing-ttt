package org.skcc.team1.legacy.customerserver.messaging;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.awspring.cloud.messaging.core.QueueMessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.text.SimpleDateFormat;

@Slf4j
public class SqsMessageSender implements MessageSender {

    private final AmazonSQSAsync sqs;
    private final long timeoutMilliSeconds;
    private final ObjectMapper objectMapper;

    @Autowired
    public SqsMessageSender(AmazonSQSAsync sqs, long timeoutMilliSeconds) {
        this.sqs= sqs;
        this.timeoutMilliSeconds = timeoutMilliSeconds;

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public void sendMessage(String queueName, DomainEventEnvelope<?> domainEventEnvelope) {
        MessageChannel messageChannel = new QueueMessageChannel(sqs, queueName);

        Message<?> message = null;
        try {
            message = MessageBuilder.withPayload(objectMapper.writeValueAsString(domainEventEnvelope))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        messageChannel.send(message, timeoutMilliSeconds);

        log.info(String.format("sent message. [%s]", domainEventEnvelope.getData()));
    }
}
