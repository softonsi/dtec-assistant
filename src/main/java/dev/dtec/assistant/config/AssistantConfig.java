package dev.dtec.assistant.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(AssistantProperties.class)
public class AssistantConfig {

    /**
     * Executor para processamento de streams SSE em background.
     * Cached thread pool é adequado para picos de conexões de streaming.
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService streamExecutor() {
        return Executors.newCachedThreadPool();
    }
}
