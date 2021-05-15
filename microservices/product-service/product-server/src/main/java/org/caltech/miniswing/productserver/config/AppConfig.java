package org.caltech.miniswing.productserver.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.PlmService;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.ServiceService;
import org.caltech.miniswing.util.AsyncHelper;
import org.caltech.miniswing.util.EnumMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
@Slf4j

public class AppConfig {
    @Value("${app.svc-service.host: 127.0.0.1}")
    private String svcHost;

    @Value("${app.svc-service.port: 8080}")
    private int svcPort;

    @Value("${app.plm-service.host: 127.0.0.1}")
    private String plmHost;

    @Value("${app.plm-service.port: 8080}")
    private int plmPort;

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("강인수");
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public AsyncHelper asyncHelper(Scheduler scheduler) {
        return new AsyncHelper(scheduler);
    }

    @Bean
    public WebClient svcWebClient() {
        return WebClient.builder()
                .baseUrl("http://" + svcHost + ":" + svcPort + "/swing/api/v1")
                .filter(
                        (req, next) -> next.exchange(
                                ClientRequest.from(req).header("from", "webclient").build()
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
                                    log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                                    clientRequest.headers().forEach(
                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                                    );
                                    return Mono.just(clientRequest);
                                }
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofResponseProcessor(
                                clientResponse -> {
                                    log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
                                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                                    return Mono.just(clientResponse);
                                }
                        )
                )
                .build();
    }

    @Bean
    public WebClient plmWebClient() {
        return WebClient.builder()
                .baseUrl("http://" + plmHost + ":" + plmPort + "/swing/api/v1")
                .filter(
                        (req, next) -> next.exchange(
                                ClientRequest.from(req).header("from", "webclient").build()
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
                                    log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                                    clientRequest.headers().forEach(
                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                                    );
                                    return Mono.just(clientRequest);
                                }
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofResponseProcessor(
                                clientResponse -> {
                                    log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
                                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                                    return Mono.just(clientResponse);
                                }
                        )
                )
                .build();
    }

    @Bean
    public ServiceClient serviceClient() {
        return ServiceService.client(svcWebClient());
    }

    @Bean
    public PlmClient plmClient() {
        return PlmService.client(plmWebClient());
    }

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        return enumMapper;
    }
}

