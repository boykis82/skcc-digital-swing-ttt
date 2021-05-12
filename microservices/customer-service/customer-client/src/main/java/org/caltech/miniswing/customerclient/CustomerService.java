package org.caltech.miniswing.customerclient;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.customerclient.dto.CustResponseDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomerService {
    public static CustomerClient client(WebClient webClient){
        return new ClientImpl(webClient);
    }

    private static class ClientImpl implements CustomerClient {
        private final WebClient webClient;

        public ClientImpl(WebClient webClient) {
            this.webClient = webClient;
        }

        @Override
        public Mono<CustResponseDto> getCustomer(long custNum) {
            log.info("CustomerService.client.getCustomer. custNum = {}", custNum);
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/customers/{custNum}")
                            .build(custNum))
                    .retrieve()
                    .bodyToMono(CustResponseDto.class);
        }
    }
}
