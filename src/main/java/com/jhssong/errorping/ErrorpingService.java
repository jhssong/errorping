package com.jhssong.errorping;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class ErrorpingService {

    private final ErrorpingProperties properties;
    private final WebClient webClient;

    public ErrorpingService(ErrorpingProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.create();
    }

    public void sendErrorToDiscord(ErrorResponse errorResponse, HttpServletRequest request) {
        Map<String, Object> payload = Map.of(
                "embeds", List.of(
                        Map.of(
                                "title",
                                "🚨 " + (errorResponse.getTitle() != null ? errorResponse.getTitle() : "Unknown Error"),
                                "description", errorResponse.getMessage() != null ? errorResponse.getMessage()
                                        : "*No detail provided.*",
                                "color", errorResponse.getStatus().value() >= 500 ? 0xFF0000 : 0xFFD700,
                                "fields", List.of(
                                        Map.of(
                                                "name", "Status Code",
                                                "value", String.valueOf(errorResponse.getStatus().value()),
                                                "inline", true
                                        ),
                                        Map.of(
                                                "name", "Method",
                                                "value", request.getMethod() != null ? request.getMethod() : "`N/A`",
                                                "inline", true
                                        ),
                                        Map.of(
                                                "name", "Instance",
                                                "value", URI.create(request.getRequestURI())
                                        )
                                ),
                                "timestamp", errorResponse.getTimestamp().toString()
                        )
                )
        );

        webClient.post()
                .uri(properties.getDiscordWebhookUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }


}
