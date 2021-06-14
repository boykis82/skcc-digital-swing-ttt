package org.caltech.miniswing.productclient;

import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductClient {
    Mono<Void> subscribeProduct(ProdSubscribeRequestDto dto);
    Mono<List<SvcProdResponseDto>> getServiceProducts(long svcMgmtNum, boolean includeTermProd);
    Mono<Void> terminateProduct(long svcMgmtNum, long svcProdId);
    Mono<Void> terminateAllProducts(long svcMgmtNum);
}
