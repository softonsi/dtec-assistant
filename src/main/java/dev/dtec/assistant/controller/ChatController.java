package dev.dtec.assistant.controller;

import dev.dtec.assistant.service.ChatStreamService;
import dev.dtec.assistant.service.PromptLoaderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final String DEFAULT_MESSAGE = "Olá! Qual sua dúvida sobre o sistema?";
    private static final String FALLBACK_MESSAGE = "Olá.";

    private final ChatStreamService chatStreamService;
    private final PromptLoaderService promptLoader;

    public ChatController(ChatStreamService chatStreamService, PromptLoaderService promptLoader) {
        this.chatStreamService = chatStreamService;
        this.promptLoader = promptLoader;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGet(@RequestParam(value = "message", defaultValue = DEFAULT_MESSAGE) String message) {
        return chatStreamService.streamToEmitter(message);
    }

    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPost(@RequestBody Map<String, String> body) {
        String message = body != null && body.containsKey("message") ? body.get("message") : FALLBACK_MESSAGE;
        return chatStreamService.streamToEmitter(message);
    }

    /**
     * Recarrega o prompt do arquivo (útil após editar o arquivo de instruções sem reiniciar a aplicação).
     */
    @PostMapping("/admin/recarregar-prompt")
    public Map<String, String> recarregarPrompt() {
        promptLoader.reload();
        return Map.of("status", "ok", "message", "Prompt recarregado.");
    }
}
