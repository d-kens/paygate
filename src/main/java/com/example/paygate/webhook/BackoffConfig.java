package com.example.paygate.webhook;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.webhook")
public class BackoffConfig {
    private int maxAttempts;
    private int[] backoffSeconds;
}
