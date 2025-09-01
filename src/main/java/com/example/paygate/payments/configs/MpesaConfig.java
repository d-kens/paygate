package com.example.paygate.payments.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mpesa")
public class MpesaConfig {
    private String authUrl;
    private String consumerKey;
    private String consumerSecret;
}
