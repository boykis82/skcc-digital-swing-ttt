package org.caltech.miniswing.productserver.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface ProductMessageChannels {
    String SERVICE_INPUT = "serviceInput";

    String PRODUCT_OUTPUT = "productOutput";

    @Input(SERVICE_INPUT)
    SubscribableChannel serviceInput();

    @Output(PRODUCT_OUTPUT)
    MessageChannel productOutput();
}


/*
1. service
  생성 시 -> SvcCreatedEvent
    product 수신 받아서 기본요금제 가입
  정지 / 해제 / 해지 시 -> SvcStatusChangedEvent
    product 수신 받아서 해지면 모든 상품 해지

2. product
  상품 가입/해지 시 -> ProductSubscriptionChangedEvent
    service 수신 받아서 기본요금제 갈아끼우기




 */