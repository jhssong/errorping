package com.jhssong.errorping;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

class ErrorpingServiceTest {
    @Test
    void send_real_discord_message() {
        String discordWebhookUrl = ""; // Fill the real webhook url to test
        if (discordWebhookUrl.isEmpty()) {
            return;
        }
        ErrorpingProperties props = new ErrorpingProperties();
        props.setDiscordWebhookUrl(discordWebhookUrl);

        ErrorpingService service = new ErrorpingService(props);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .title("Integration Test Error")
                .message("This is a real Discord webhook test")
                .status(500)
                .build();

        // HttpServletRequest mock
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test/api");

        service.sendErrorToDiscord(errorResponse, request);
    }
}