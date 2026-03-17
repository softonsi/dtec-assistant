package dev.dtec.assistant.service;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantService {

    private final ChatModel chatModel;
    private final StreamingChatModel streamingChatModel;
    private final PromptLoaderService promptLoader;

    private static final String FALLBACK_SYSTEM = "Você é um assistente útil. Responda de forma clara e objetiva com base no contexto disponível.";

    public AssistantService(ChatModel chatModel, StreamingChatModel streamingChatModel, PromptLoaderService promptLoader) {
        this.chatModel = chatModel;
        this.streamingChatModel = streamingChatModel;
        this.promptLoader = promptLoader;
    }

    public String getSystemContent() {
        String content = promptLoader.getPromptContent();
        return (content != null && !content.isBlank()) ? content : FALLBACK_SYSTEM;
    }

    public void stream(String userMessage, StreamingChatResponseHandler handler) {
        SystemMessage systemMessage = SystemMessage.from(getSystemContent());
        UserMessage userMsg = UserMessage.from(userMessage);

        streamingChatModel.chat(List.of(systemMessage, userMsg), handler);
    }
}
