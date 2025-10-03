package com.example.paygate.novu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
public class NovuService {
    private final NovuConfig novuConfig;
    private final RestClient restClient;

    public NovuService(RestClient.Builder restClientBuilder,
                                NovuConfig novuConfig) {
        this.novuConfig = novuConfig;
        this.restClient = restClientBuilder
                .baseUrl(novuConfig.getApiUrl())
                .defaultHeader("Authorization", "ApiKey " + novuConfig.getApiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("Novu service initialized with URL: {}", novuConfig.getApiUrl());
    }


    public String triggerNotification(String eventName, String subscriberId, Map<String, Object> payload) {
        try {

        } catch (Exception e) {

        }

        return "";
    }

    public void upsertSubscriber() {}
}