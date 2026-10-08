package com.ejemplo.productos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    private final SupabaseProperties properties;

    public RestClientConfig(SupabaseProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClient supabaseRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getTimeout()))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofMillis(properties.getTimeout()));

        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .requestFactory(factory)
                .defaultHeader("apikey", properties.getKey())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}