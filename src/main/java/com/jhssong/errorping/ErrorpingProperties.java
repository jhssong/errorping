package com.jhssong.errorping;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "errorping")
public class ErrorpingProperties {
    @NotBlank
    private String discordWebhookUrl;

    private String discordUsername = "Errorping";

    private String discordAvatarUrl = "https://raw.githubusercontent.com/jhssong/errorping/main/assets/errorping-logo.png";
}
