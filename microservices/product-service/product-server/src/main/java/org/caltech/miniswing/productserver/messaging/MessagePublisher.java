package org.caltech.miniswing.productserver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.event.DomainEventEnvelope;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.caltech.miniswing.productclient.event.ProductSubscriptionChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EnableBinding(ProductMessageChannels.class)
public class MessagePublisher {
    private final ProductMessageChannels messageChannels;

    @Autowired
    public MessagePublisher(ProductMessageChannels messageChannels) {
        this.messageChannels = messageChannels;
    }

    //-- 상품 가입 내역에 변경 발생 시 이벤트 발행
    public void publishProductSubscriptionChangedEvent(long svcMgmtNum, List<SvcProdResponseDto> svcProdResponseDtos) {
        List<SvcProdResponseDto> subProds = svcProdResponseDtos.stream()
                .filter(sp -> sp.getTermDt() == null)
                .collect(Collectors.toList());

        List<SvcProdResponseDto> termProds = svcProdResponseDtos.stream()
                .filter(sp -> sp.getTermDt() != null)
                .collect(Collectors.toList());

        Message<?> msg = MessageBuilder.withPayload(
                new DomainEventEnvelope<>(
                        new ProductSubscriptionChangedEvent(svcMgmtNum, subProds, termProds)
                )
        ).setHeader("type", "ProductSubscriptionChanged").build();

        log.info(String.format("[MQ] publishProductSubscriptionChangedEvent. svcMgmtNum = [%d], dto = [%s]", svcMgmtNum, svcProdResponseDtos));

        messageChannels.productOutput().send(msg);
    }
}
