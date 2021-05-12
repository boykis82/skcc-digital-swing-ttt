package org.caltech.miniswing.serviceclient;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.serviceclient.dto.SvcResponseDto;
import org.caltech.miniswing.serviceclient.dto.SvcUpdateRequestDto;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ServiceService {
    public static ServiceClient client(WebClient webClient){
        return new ClientImpl(webClient);
    }

    private static class ClientImpl implements ServiceClient {
        private final WebClient webClient;

        public ClientImpl(WebClient webClient) {
            this.webClient = webClient;
        }

        @Override
        public Mono<SvcResponseDto> getService(long svcMgmtNum) {
            log.info("ServiceService.client.getService. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .retrieve()
                    .bodyToMono(SvcResponseDto.class);
        }

        @Override
        public Flux<SvcResponseDto> getServicesByCustNum(int offset,
                                                  int limit,
                                                  long custNum,
                                                  boolean includeTermSvc) {
            log.info("ServiceService.client.getServicesByCustNum. custNum = {}, includeTermSvc = {}, offset = {}, limit = {}", custNum, includeTermSvc, offset, limit);
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services")
                            .queryParam("cust_num", custNum)
                            .queryParam("include_term_svc", includeTermSvc)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToFlux(SvcResponseDto.class);
        }

        @Override
        public Mono<Void> activateService(long svcMgmtNum) {
            log.info("ServiceService.client.activateService. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .body(BodyInserters.fromValue(SvcUpdateRequestDto.createActivateServiceDto()))
                    .retrieve()
                    .bodyToMono(Void.class);
        }

        @Override
        public Mono<Void> suspendService(long svcMgmtNum) {
            log.info("ServiceService.client.suspendService. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .body(BodyInserters.fromValue(SvcUpdateRequestDto.createSuspendServiceDto()))
                    .retrieve()
                    .bodyToMono(Void.class);
        }

        @Override
        public Mono<Void> terminateServiceByAuthority(long svcMgmtNum) {
            log.info("ServiceService.client.terminateServiceByAuthority. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .body(BodyInserters.fromValue(SvcUpdateRequestDto.createTerminateServiceDto()))
                    .retrieve()
                    .bodyToMono(Void.class);
        }

        @Override
        public Mono<Void> changeBasicProduct(long svcMgmtNum, String feeProdId) {
            log.info("ServiceService.client.changeBasicProduct. svcMgmtNum = {}, prodId = {}", svcMgmtNum, feeProdId);
            return webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .body(BodyInserters.fromValue(SvcUpdateRequestDto.createChangeBasicProdDto(feeProdId)))
                    .retrieve()
                    .bodyToMono(Void.class);
        }
    }
}
