package org.caltech.miniswing.serviceserver.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
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

import javax.persistence.EntityManager;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
@Slf4j
@Getter
public class AppConfig {
    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    private WebClient plmWebClient;
    private WebClient productWebClient;

    @Bean
    public WebClient plmWebClient() {
        if (plmWebClient == null) {
            plmWebClient = FilteredWebClient.create(webClientBuilder, "http://plm/swing/api/v1");
        }
        return plmWebClient;
    }

    @Bean
    public PlmClient plmClient() {
        return PlmService.client(plmWebClient());
    }

    @Bean
    public WebClient productWebClient() {
        if (productWebClient == null) {
            productWebClient = FilteredWebClient.create(webClientBuilder, "http://product/swing/api/v1");
        }
        return productWebClient;
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

