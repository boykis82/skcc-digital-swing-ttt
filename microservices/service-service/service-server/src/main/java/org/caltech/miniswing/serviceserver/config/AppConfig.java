package org.caltech.miniswing.serviceserver.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.PlmService;
import org.caltech.miniswing.productclient.ProductClient;
import org.caltech.miniswing.productclient.ProductService;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.ServiceService;
import org.caltech.miniswing.serviceclient.dto.SvcStCd;
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

import javax.persistence.EntityManager;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
@Slf4j
public class AppConfig {
    @Value("${app.plm-service.host:127.0.0.1}")
    private String plmHost;

    @Value("${app.plm-service.port:8080}")
    private int plmPort;

    @Value("${app.prod-service.host:127.0.0.1}")
    private String prodHost;

    @Value("${app.prod-service.port:8080}")
    private int prodPort;

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
    public PlmClient plmClient() {
        return PlmService.client(plmWebClient());
    }

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl("http://" + prodHost + ":" + prodPort + "/swing/api/v1")
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
    public ProductClient productClient() {
        return ProductService.client(productWebClient());
    }

    @Bean
    public AsyncHelper asyncHelper(Scheduler scheduler) {
        return new AsyncHelper(scheduler);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("강인수");
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("svcStCd", SvcStCd.class);
        return enumMapper;
    }
}

