package com.jhssong.errorping;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class ErrorpingService {

    private final ErrorpingProperties properties;
    private final WebClient webClient;

    public ErrorpingService(ErrorpingProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.create();
    }

    public void sendErrorToDiscord(Exception e, HttpStatus status, HttpServletRequest request, String message) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String requestUrl = request.getRequestURL().toString();
        String method = request.getMethod();
        String exceptionClass = e != null ? e.getClass().getSimpleName() : "Unknown";

        String description = """
                **Error occurred! (%s)**
                
                **Status Code**
                %d (%s)
                
                **Exception**
                %s
                
                **Request URL**
                [%s] %s
                
                **Error Message**
                %s
                """
                .formatted(now, status.value(), status.getReasonPhrase(), exceptionClass, method, requestUrl, message);

        Map<String, Object> payload = Map.of(
                "embeds", List.of(
                        Map.of(
                                "color", status.value() >= 500 ? 0xFF0000 : 0xFFD700,
                                "description", description
                        )
                )
        );

        webClient.post()
                .uri(properties.getDiscordWebhookUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        response -> log.debug("[Errorping] Discord webhook sent successfully"),
                        error -> log.error("[Errorping] Failed to send message to Discord", error)
                );
    }


}
