package org.caltech.miniswing.serviceclient;

import org.caltech.miniswing.serviceclient.dto.SvcResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceClient {
    Mono<SvcResponseDto> getService(long svcMgmtNum);
    Flux<SvcResponseDto> getServicesByCustNum(int offset,
                                              int limit,
                                              long custNum,
                                              boolean includeTermSvc);
    Mono<Void> activateService(long svcMgmtNum);
    Mono<Void> suspendService(long svcMgmtNum);
    Mono<Void> terminateServiceByAuthority(long svcMgmtNum);
    Mono<Void> changeBasicProduct(long svcMgmtNum, String feeProdId);
}
