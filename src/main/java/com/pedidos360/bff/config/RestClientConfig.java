package com.pedidos360.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.orders.url}")
    private String ordersUrl;

    @Value("${services.catalog.url}")
    private String catalogUrl;

    @Bean
    public RestClient ordersClient() {
        return RestClient.builder().baseUrl(ordersUrl).build();
    }

    @Bean
    public RestClient catalogClient() {
        return RestClient.builder().baseUrl(catalogUrl).build();
    }
}
