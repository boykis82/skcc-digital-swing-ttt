package org.caltech.miniswing.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswing.customerclient.CustomerClient;
import org.caltech.miniswing.customerclient.CustomerService;
import org.caltech.miniswing.productclient.ProductClient;
import org.caltech.miniswing.productclient.ProductService;
import org.caltech.miniswing.serviceclient.ServiceClient;
import org.caltech.miniswing.serviceclient.ServiceService;
import org.caltech.miniswing.util.http.FilteredWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private WebClient svcWebClient;
    private WebClient prodWebClient;

    @Bean
    public WebClient svcWebClient() {
        if (svcWebClient == null) {
            svcWebClient = FilteredWebClient.create(webClientBuilder, "http://service/swing/api/v1");
        }
        return svcWebClient;
    }

    @Bean
    public WebClient prodWebClient() {
        if (prodWebClient == null) {
            prodWebClient = FilteredWebClient.create(webClientBuilder, "http://product/swing/api/v1");
        }
        return prodWebClient;
    }

    @Bean
    public WebClient custWebClient() {
        if (custWebClient == null) {
            custWebClient = FilteredWebClient.create(webClientBuilder, "http://customer/swing/api/v1");
        }
        return custWebClient;
    }

    @Bean
    public ProductClient productClient() {
        return ProductService.client(prodWebClient());
    }

    @Bean
    public CustomerClient customerClient() {
        return CustomerService.client(custWebClient());
    }

    @Bean
    public ServiceClient serviceClient() {
        return ServiceService.client(svcWebClient());
    }

}
