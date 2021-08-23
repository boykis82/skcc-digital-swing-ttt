package org.caltech.miniswing.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.skcc.team1.legacy.customerclient.CustomerClient;
import org.skcc.team1.legacy.customerclient.CustomerService;
import org.caltech.miniswing.util.http.FilteredWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class AppConfig {

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    private WebClient custWebClient;

    @Bean
    public WebClient custWebClient() {
        if (custWebClient == null) {
            custWebClient = FilteredWebClient.create(webClientBuilder, "http://customer/swing/api/v1");
        }
        return custWebClient;
    }

    @Bean
    public CustomerClient customerClient() {
        return CustomerService.client(custWebClient());
    }

}
