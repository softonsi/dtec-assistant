package dev.dtec.assistant.service;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Service
public class ChatStreamService {

    private static final long STREAM_TIMEOUT_MS = 300_000L; // 5 minutos

    private final AssistantService assistantService;
    private final ExecutorService streamExecutor;

    public ChatStreamService(AssistantService assistantService, ExecutorService streamExecutor) {
        this.assistantService = assistantService;
        this.streamExecutor = streamExecutor;
    }
    public SseEmitter streamToEmitter(String message) {
        SseEmitter emitter = new SseEmitter(STREAM_TIMEOUT_MS);

        streamExecutor.execute(() -> {
            try {
                assistantService.stream(message, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String partialResponse) {
                        try {
                            emitter.send(SseEmitter.event().data(partialResponse));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse completeResponse) {
                        try {
                            emitter.complete();
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        emitter.completeWithError(error);
                    }
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(emitter::complete);
        emitter.onError((e) -> {});
        return emitter;
    }
}