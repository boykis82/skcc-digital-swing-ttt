package org.caltech.miniswing.plmclient;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.dto.ProdResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class PlmService {
    public static PlmClient client(WebClient webClient){
        return new ClientImpl(webClient);
    }

    private static class ClientImpl implements PlmClient {
        private final WebClient webClient;

        public ClientImpl(WebClient webClient) {
            this.webClient = webClient;
        }

        @Override
        public Mono<List<ProdResponseDto>> getProdNmByIds(List<String> prodIds) {
            log.info("PlmService.client.getProdNmByIds. prodIds = {}", String.join(",", prodIds));
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/plm")
                            .queryParam("prodIds", String.join(",", prodIds))
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ProdResponseDto>>() {});
        }
    }
}
