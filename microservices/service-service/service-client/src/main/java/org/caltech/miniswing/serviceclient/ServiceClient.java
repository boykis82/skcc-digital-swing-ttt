package org.caltech.miniswing.serviceclient;

import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ServiceClient {
    Mono<ServiceDto> getService(long svcMgmtNum);
    Mono<List<ServiceDto>> getServicesByCustNum(int offset,
                                                int limit,
                                                long custNum,
                                                boolean includeTermSvc);
    //Mono<Void> activateService(long svcMgmtNum);
    //Mono<Void> suspendService(long svcMgmtNum);
    //Mono<Void> terminateServiceByAuthority(long svcMgmtNum);
    //Mono<Void> changeBasicProduct(long svcMgmtNum, String feeProdId);
}
