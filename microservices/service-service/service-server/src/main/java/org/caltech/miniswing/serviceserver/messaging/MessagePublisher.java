package org.caltech.miniswing.serviceserver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.caltech.miniswing.exception.InvalidInputException;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceclient.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@EnableBinding(SvcMessageChannels.class)
public class MessagePublisher {
    private final SvcMessageChannels messageChannels;

    @Autowired
    public MessagePublisher(SvcMessageChannels messageChannels) {
        this.messageChannels = messageChannels;
    }

    public void publishServiceCreatedEvent(ServiceDto svc) {
        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        ServiceCreatedEvent.builder()
                                .svcMgmtNum(svc.getSvcMgmtNum())
                                .custNum(svc.getCustNum())
                                .feeProdId(svc.getFeeProdId())
                                .svcCd(svc.getSvcCd())
                                .svcScrbDt(svc.getSvcScrbDt())
                                .svcStCd(svc.getSvcStCd())
                                .build()
                )
        ).setHeader("type", "ServiceCreated").build();

        log.info( String.format("[MQ] ServiceCreatedEvent sent! payload = [%s]", msg) );

        messageChannels.serviceOutput().send(msg);
    }

    public void publishServiceActivatedEvent(long svcMgmtNum, SvcStCd beforeSvcStCd) {
        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new ServiceActivatedEvent(svcMgmtNum, beforeSvcStCd)
                )
        ).setHeader("type", "ServiceActivated").build();

        log.info( String.format("[MQ] ServiceActivatedEvent sent! payload = [%s]", msg) );

        messageChannels.serviceOutput().send(msg);
    }

    public void publishServiceSuspendedEvent(long svcMgmtNum, SvcStCd beforeSvcStCd) {
        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new ServiceSuspendedEvent(svcMgmtNum, beforeSvcStCd)
                )
        ).setHeader("type", "ServiceSuspended").build();

        log.info( String.format("[MQ] ServiceSuspendedEvent sent! payload = [%s]", msg) );

        messageChannels.serviceOutput().send(msg);
    }

    public void publishServiceTerminatedEvent(long svcMgmtNum, SvcStCd beforeSvcStCd) {
        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new ServiceTerminatedEvent(svcMgmtNum, beforeSvcStCd)
                )
        ).setHeader("type", "ServiceTerminated").build();

        log.info( String.format("[MQ] ServiceTerminatedEvent sent! payload = [%s]", msg) );

        messageChannels.serviceOutput().send(msg);
    }
}
