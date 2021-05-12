package org.caltech.miniswing.plmclient;

import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlmClient {
    Mono<List<ProdResponseDto>> getProdNmByIds(List<String> prodIds);
}
