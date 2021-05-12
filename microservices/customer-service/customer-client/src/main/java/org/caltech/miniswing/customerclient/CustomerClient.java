package org.caltech.miniswing.customerclient;

import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import reactor.core.publisher.Mono;

public interface CustomerClient {
    Mono<CustResponseDto> getCustomer(long custNum);
}
