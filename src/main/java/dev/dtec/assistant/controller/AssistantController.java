package dev.dtec.assistant;

import dev.dtec.assistant.service.AssistantService;
import dev.dtec.assistant.service.ChatStreamService;
import dev.dtec.assistant.service.PromptLoaderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * API do assistente: responde com base no conteúdo do arquivo prompt.txt (system message).
 * Coloque suas regras, FAQ e contexto em src/main/resources/prompt.txt (ou path configurado).
 */
@RestController
public class AssistantController {

    private static final String DEFAULT_MESSAGE = "Olá, como você pode me ajudar?";
    private static final String FALLBACK_MESSAGE = "Olá.";

    private final AssistantService assistantService;
    private final ChatStreamService chatStreamService;
    private final PromptLoaderService promptLoader;

    public AssistantController(AssistantService assistantService, ChatStreamService chatStreamService, PromptLoaderService promptLoader) {
        this.assistantService = assistantService;
        this.chatStreamService = chatStreamService;
        this.promptLoader = promptLoader;
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStreamGet(@RequestParam(value = "message", defaultValue = DEFAULT_MESSAGE) String message) {
        return chatStreamService.streamToEmitter(message);
    }

    @PostMapping(value = "/chat/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStreamPost(@RequestBody Map<String, String> body) {
        String message = body != null && body.containsKey("message") ? body.get("message") : FALLBACK_MESSAGE;
        return chatStreamService.streamToEmitter(message);
    }

    /**
     * Recarrega o prompt do arquivo (útil após editar prompt.txt sem reiniciar a aplicação).
     */
    @PostMapping("/admin/reload-prompt")
    public Map<String, String> reloadPrompt() {
        promptLoader.reload();
        return Map.of("status", "ok", "message", "Prompt recarregado.");
    }
}
