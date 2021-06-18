package org.caltech.miniswing.serviceserver.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface SvcMessageChannels {
    String BILLING_INPUT = "billingInput";
    String PRODUCT_INPUT = "productInput";

    String SERVICE_OUTPUT = "serviceOutput";

    @Input(BILLING_INPUT)
    SubscribableChannel billingInput();

    @Input(PRODUCT_INPUT)
    SubscribableChannel productInput();

    @Output(SERVICE_OUTPUT)
    MessageChannel serviceOutput();
}
