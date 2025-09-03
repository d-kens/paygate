package com.example.paygate.payments.providers.mpesa;

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
    private String shortCode;
    private String callBackUrl;
    private String consumerKey;
    private String consumerSecret;
}
