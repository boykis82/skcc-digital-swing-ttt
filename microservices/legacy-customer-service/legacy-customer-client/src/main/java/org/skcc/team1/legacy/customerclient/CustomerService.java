package org.skcc.team1.legacy.customerclient;

import lombok.extern.slf4j.Slf4j;
import org.skcc.team1.legacy.customerclient.dto.CustResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

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

        @Override
        public Mono<List<CustResponseDto>> getCustomers(String customerName, LocalDate birthday) {
            log.info("CustomerService.client.getCustomers. customerName = {}, birthday = {}", customerName, birthday);
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/customers")
                            .queryParam("custNm", customerName)
                            .queryParam("birthDt", birthday)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CustResponseDto>>() {});
        }
    }
}
