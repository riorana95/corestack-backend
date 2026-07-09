package com.xora.backend;

import com.xora.backend.config.JwtProperties;
import com.xora.backend.config.GoogleOAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, GoogleOAuthProperties.class})
public class XoraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(XoraBackendApplication.class, args);
    }
}
