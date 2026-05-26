package com.corestack.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.google")
public record GoogleOAuthProperties(
        @DefaultValue("") String clientId
) {
}
