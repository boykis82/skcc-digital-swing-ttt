package org.caltech.miniswing.productclient;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.productclient.dto.ProdSubscribeRequestDto;
import org.caltech.miniswing.productclient.dto.SvcProdResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class ProductService {
    public static ProductClient client(WebClient webClient){
        return new ClientImpl(webClient);
    }

    private static class ClientImpl implements ProductClient {
        private final WebClient webClient;

        public ClientImpl(WebClient webClient) {
            this.webClient = webClient;
        }

        @Override
        public Mono<Void> subscribeProduct(ProdSubscribeRequestDto dto) {
            log.info("ProductService.client.subscribeProduct. dto = {}", dto);
            return webClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/products")
                            .build())
                    .body(BodyInserters.fromValue(dto))
                    .retrieve()
                    .bodyToMono(Void.class);
        }

        @Override
        public Mono<List<SvcProdResponseDto>> getServiceProducts(long svcMgmtNum, boolean includeTermProd) {
            log.info("ProductService.client.getServiceProducts. svcMgmtNum = {}, includeTermProd = {}", svcMgmtNum, includeTermProd);
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/products")
                            .queryParam("svcMgmtNum", svcMgmtNum)
                            .queryParam("includeTermProd", includeTermProd)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SvcProdResponseDto>>() {});
        }

        @Override
        public Mono<Void> terminateProduct(long svcMgmtNum, long svcProdId) {
            log.info("ProductService.client.terminateProduct. svcProdId = {}", svcProdId);
            return webClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/products/{svcProdId}")
                            .queryParam("svcMgmtNum", svcMgmtNum)
                            .build(svcProdId))
                    .retrieve()
                    .bodyToMono(Void.class);
        }

        @Override
        public Mono<Void> terminateAllProducts(long svcMgmtNum) {
            log.info("ProductService.client.terminateAllProducts. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/products")
                            .queryParam("svcMgmtNum", svcMgmtNum)
                            .build())
                    .retrieve()
                    .bodyToMono(Void.class);
        }
    }
}
