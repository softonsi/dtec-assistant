package dev.dtec.assistant.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.http.HttpTimeoutException;
import java.util.Map;

/**
 * Tratamento global de exceções da API.
 * Converte erros de timeout e de rede em respostas HTTP adequadas (503/504).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpTimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeout(HttpTimeoutException e) {
        log.warn("Timeout na chamada ao modelo de IA: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(Map.of("error", "timeout", "message", "A requisição ao assistente expirou. Tente novamente."));
    }

    @ExceptionHandler(org.springframework.web.client.ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleResourceAccess(org.springframework.web.client.ResourceAccessException e) {
        Throwable cause = e.getCause();
        if (cause instanceof HttpTimeoutException) {
            return handleTimeout((HttpTimeoutException) cause);
        }
        log.warn("Erro de rede na chamada ao modelo de IA: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "service_unavailable", "message", "O assistente está temporariamente indisponível. Tente novamente."));
    }
}
