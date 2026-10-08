package com.ejemplo.productos.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(DotenvConfig.class);

    static {
        try {
            logger.info("Cargando variables de entorno desde .env");

            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .ignoreIfMalformed()
                    .load();

            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                logger.debug("Variable cargada: {}", entry.getKey());
            });

            logger.info("Variables de entorno cargadas correctamente");

        } catch (Exception e) {
            logger.warn("No se pudo cargar el archivo .env: {}", e.getMessage());
        }
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // Las variables ya fueron cargadas en el bloque static
    }
}