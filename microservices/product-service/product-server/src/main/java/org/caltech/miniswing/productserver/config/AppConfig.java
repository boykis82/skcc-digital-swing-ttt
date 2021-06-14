package org.caltech.miniswing.productserver.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.plmclient.PlmClient;
import org.caltech.miniswing.plmclient.PlmService;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.ServiceService;
import org.caltech.miniswing.util.AsyncHelper;
import org.caltech.miniswing.util.EnumMapper;
import org.caltech.miniswing.util.http.FilteredWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    private WebClient plmWebClient;
    private WebClient svcWebClient;

    @Bean
    public WebClient svcWebClient() {
        if (svcWebClient == null) {
            svcWebClient = FilteredWebClient.create(webClientBuilder, "http://service/swing/api/v1");
        }
        return svcWebClient;
    }

    @Bean
    public WebClient plmWebClient() {
        if (plmWebClient == null) {
            plmWebClient = FilteredWebClient.create(webClientBuilder, "http://plm/swing/api/v1");
        }
        return plmWebClient;
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

