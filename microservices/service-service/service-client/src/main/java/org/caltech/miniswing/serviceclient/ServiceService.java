package org.caltech.miniswing.serviceclient;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.serviceclient.dto.ServiceDto;
import org.caltech.miniswing.serviceclient.dto.ServiceStatusChangeRequestDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
        public Mono<ServiceDto> getService(long svcMgmtNum) {
            log.info("[API] ServiceService.client.getService. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .retrieve()
                    .bodyToMono(ServiceDto.class);
        }

        @Override
        public Mono<List<ServiceDto>> getServicesByCustNum(int offset,
                                                           int limit,
                                                           long custNum,
                                                           boolean includeTermSvc) {
            log.info("[API] ServiceService.client.getServicesByCustNum. custNum = {}, includeTermSvc = {}, offset = {}, limit = {}", custNum, includeTermSvc, offset, limit);
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
                    .bodyToMono(new ParameterizedTypeReference<List<ServiceDto>>() {});
        }

        /*
        @Override
        public Mono<Void> activateService(long svcMgmtNum) {
            log.info("ServiceService.client.activateService. svcMgmtNum = {}", svcMgmtNum);
            return webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/services/{svcMgmtNum}")
                            .build(svcMgmtNum))
                    .body(BodyInserters.fromValue(ServiceStatusChangeRequestDto.createActivateServiceDto()))
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
                    .body(BodyInserters.fromValue(ServiceStatusChangeRequestDto.createSuspendServiceDto()))
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
                    .body(BodyInserters.fromValue(ServiceStatusChangeRequestDto.createTerminateServiceDto()))
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
                    .body(BodyInserters.fromValue(SvcStatusChangeRequestDto.createChangeBasicProdDto(feeProdId)))
                    .retrieve()
                    .bodyToMono(Void.class);
        }
         */
    }
}
