package com.ejemplo.productos.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseProperties.class);

    private String url;
    private String key;
    private int timeout = 5000;

    @PostConstruct
    public void init() {
        logger.info("SupabaseProperties cargado - URL: {}", url);
        logger.info("SupabaseProperties cargado - Key: {}", key != null ? "***" + key.substring(Math.max(0, key.length() - 4)) : "NULL");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}