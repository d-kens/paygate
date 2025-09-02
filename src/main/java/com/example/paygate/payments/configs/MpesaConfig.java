package com.example.paygate.payments.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mpesa")
public class MpesaConfig {
    private String passKey;
    private String authUrl;
    private String stkPushUrl;
    private String callBackUrl;
    private String consumerKey;
    private String stkShortCode;
    private String consumerSecret;
}
