package org.caltech.miniswing.serviceserver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.billingclient.event.UnpaidFeeClearedEvent;
import org.caltech.miniswing.billingclient.event.UnpaidFeeOccurredEvent;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.caltech.miniswing.plmclient.dto.SvcProdCd;
import org.caltech.miniswing.productclient.event.ProductSubscriptionChangedEvent;
import org.caltech.miniswing.serviceclient.dto.ServiceStatusChangeRequestDto;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
import org.caltech.miniswing.serviceserver.service.SvcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@Slf4j
@EnableBinding(SvcMessageChannels.class)
public class MessageConsumer {
    private final SvcService svcService;

    @Autowired
    public MessageConsumer(SvcService svcService) {
        this.svcService = svcService;
    }

    @StreamListener(value = SvcMessageChannels.BILLING_INPUT, condition = "headers['type'] == 'UnpaidFeeOccurred'")
    public void processUnpaidFeeOccurredEvent(DomainEventEnvelope<UnpaidFeeOccurredEvent> dee) {
        UnpaidFeeOccurredEvent e = dee.getData();
        log.info( String.format("[MQ] UnpaidFeeOccurredEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        svcService.changeServiceStatus(e.getSvcMgmtNum(),
                ServiceStatusChangeRequestDto.builder()
                        .afterSvcStCd(SvcStCd.SP)
                        .build()
        );
    }

    @StreamListener(value = SvcMessageChannels.BILLING_INPUT, condition = "headers['type'] == 'UnpaidFeeCleared'")
    public void processUnpaidFeeClearedEvent(DomainEventEnvelope<UnpaidFeeClearedEvent> dee) {
        UnpaidFeeClearedEvent e = dee.getData();
        log.info( String.format("[MQ] UnpaidFeeClearedEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        svcService.changeServiceStatus(e.getSvcMgmtNum(),
                ServiceStatusChangeRequestDto.builder()
                        .afterSvcStCd(SvcStCd.AC)
                        .build()
        );
    }

    @StreamListener(value = SvcMessageChannels.PRODUCT_INPUT, condition = "headers['type'] == 'ProductSubscriptionChanged'")
    public void processProductSubscriptionChanged(DomainEventEnvelope<ProductSubscriptionChangedEvent> dee) {
        ProductSubscriptionChangedEvent e = dee.getData();
        log.info( String.format("[MQ] ProductSubscriptionChangedEvent received! created at = [%s], payload = [%s]", dee.getEventCreatedAt(), e) );

        e.getSubscribedProds()
                .stream()
                .filter(p -> p.getSvcProdCd() == SvcProdCd.P1)
                .findFirst()
                .ifPresent(sp -> svcService.changeFeeProduct(e.getSvcMgmtNum(), sp.getProdId()));
    }
}
