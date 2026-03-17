package dev.dtec.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "assistant")
public class AssistantProperties {

    private String promptPath = "classpath:instructions/prompt.txt";

    private String promptEncoding = "UTF-8";

    public String getPromptPath() {
        return promptPath;
    }

    public void setPromptPath(String promptPath) {
        this.promptPath = promptPath;
    }

    public String getPromptEncoding() {
        return promptEncoding;
    }

    public void setPromptEncoding(String promptEncoding) {
        this.promptEncoding = promptEncoding;
    }
}
