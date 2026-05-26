package com.corestack.backend;

import com.corestack.backend.config.JwtProperties;
import com.corestack.backend.config.GoogleOAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, GoogleOAuthProperties.class})
public class CorestackBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorestackBackendApplication.class, args);
    }
}
