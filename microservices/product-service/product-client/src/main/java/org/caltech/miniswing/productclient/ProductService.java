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
        public Mono<Void> subscribeProduct(long svcMgmtNum, ProdSubscribeRequestDto dto) {
            log.info("ProductService.client.subscribeProduct. svcMgmtNum = {}, dto = {}", svcMgmtNum, dto);
            return webClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}/products")
                            .build(svcMgmtNum))
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
                            .path("/services/{svcMgmtNum}/products")
                            .queryParam("includeTermProd", includeTermProd)
                            .build(svcMgmtNum))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SvcProdResponseDto>>() {});
        }

        @Override
        public Mono<Void> terminateProduct(long svcMgmtNum, long svcProdId) {
            log.info("ProductService.client.terminateProduct. svcMgmtNum = {}, svcProdId = {}", svcMgmtNum, svcProdId);
            return webClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}/products/{svcProdId}")
                            .build(svcMgmtNum, svcProdId))
                    .retrieve()
                    .bodyToMono(Void.class);
        }

        @Override
        public Mono<Void> terminateAllProducts(long svcMgmtNum) {
            log.info("ProductService.client.terminateAllProducts. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}/products")
                            .build(svcMgmtNum))
                    .retrieve()
                    .bodyToMono(Void.class);
        }
    }
}
