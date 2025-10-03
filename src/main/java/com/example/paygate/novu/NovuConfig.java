package com.example.paygate.novu;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.novu")
public class NovuConfig {
    private String apiUrl = "https://api.novu.co/v1";
    private String apiKey;
}
