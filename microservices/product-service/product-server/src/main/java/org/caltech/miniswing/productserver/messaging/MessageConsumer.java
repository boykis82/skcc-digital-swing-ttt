package org.caltech.miniswing.productserver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.caltech.miniswing.exception.NotImplementedException;
import org.caltech.miniswing.exception.UnknownEventException;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productserver.service.ProdService;
import org.caltech.miniswing.serviceclient.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@Slf4j
@EnableBinding(ProductMessageChannels.class)
public class MessageConsumer {
    private final ProdService prodService;

    @Autowired
    public MessageConsumer(ProdService prodService) {
        this.prodService = prodService;
    }

    @StreamListener(value = ProductMessageChannels.SERVICE_INPUT, condition = "headers['type'] == 'ServiceCreated'")
    public void processServiceCreatedEvent(DomainEventEnvelope<ServiceCreatedEvent> dee) {
        ServiceCreatedEvent e = dee.getData();
        log.info( String.format("[MQ] ServiceCreatedEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        prodService.subscribeProduct(
                ProdSubscribeRequestDto.builder()
                        .svcMgmtNum(e.getSvcMgmtNum())
                        .svcProdCd(SvcProdCd.P1)
                        .prodId(e.getFeeProdId())
                        .build()
        );
    }

    @StreamListener(value = ProductMessageChannels.SERVICE_INPUT, condition = "headers['type'] == 'ServiceActivated'")
    public void processServiceActivatedEvent(DomainEventEnvelope<ServiceActivatedEvent> dee) {
        ServiceActivatedEvent e = dee.getData();
        log.info( String.format("[MQ] ServiceActivatedEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        throw new NotImplementedException("implement me!");
    }

    @StreamListener(value = ProductMessageChannels.SERVICE_INPUT, condition = "headers['type'] == 'ServiceSuspended'")
    public void processServiceSuspendedEvent(DomainEventEnvelope<ServiceSuspendedEvent> dee) {
        ServiceSuspendedEvent e = dee.getData();
        log.info( String.format("[MQ] ServiceSuspendedEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        throw new NotImplementedException("implement me!");
    }

    @StreamListener(value = ProductMessageChannels.SERVICE_INPUT, condition = "headers['type'] == 'ServiceTerminated'")
    public void processServiceTerminatedEvent(DomainEventEnvelope<ServiceTerminatedEvent> dee) {
        ServiceTerminatedEvent e = dee.getData();
        log.info( String.format("[MQ] ServiceTerminatedEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        prodService.terminateAllProducts(e.getSvcMgmtNum());
    }
}
