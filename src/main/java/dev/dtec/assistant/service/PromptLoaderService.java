package dev.dtec.assistant.service;

import dev.dtec.assistant.config.AssistantProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class PromptLoaderService {

    private static final Logger log = LoggerFactory.getLogger(PromptLoaderService.class);

    private final ResourceLoader resourceLoader;
    private final AssistantProperties properties;

    private String promptContent;

    public PromptLoaderService(ResourceLoader resourceLoader, AssistantProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    @PostConstruct
    public void loadPrompt() {
        String path = properties.getPromptPath();
        Charset charset = Charset.forName(properties.getPromptEncoding());
        try {
            Resource resource = resourceLoader.getResource(path);
            if (!resource.exists()) {
                log.warn("Arquivo de prompt não encontrado: {}. Usando prompt vazio.", path);
                promptContent = "";
                return;
            }
            try (var input = resource.getInputStream()) {
                promptContent = new String(input.readAllBytes(), charset);
            }
            log.info("Prompt carregado de {} ({} caracteres).", path, promptContent.length());
        } catch (IOException e) {
            log.error("Erro ao carregar prompt de {}: {}", path, e.getMessage());
            promptContent = "";
        }
    }

    public String getPromptContent() {
        return promptContent != null ? promptContent : "";
    }

    public void reload() {
        loadPrompt();
    }
}
