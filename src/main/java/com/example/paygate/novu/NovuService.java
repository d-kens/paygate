package com.example.paygate.novu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class NovuService {
    private final NovuConfig novuConfig;
    private final RestClient restClient;

    public NovuService(
            NovuConfig novuConfig,
            RestClient.Builder restClientBuilder
    ) {
        this.novuConfig = novuConfig;
        this.restClient = restClientBuilder
                .baseUrl(novuConfig.getApiUrl())
                .defaultHeader("Authorization", "ApiKey " + novuConfig.getApiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("Novu service initialized with URL: {}", novuConfig.getApiUrl());
    }


    public String triggerNotification(String eventName, String subscriberId, Map<String, Object> payload) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", eventName);
        requestBody.put("to", Map.of("subscriberId", subscriberId));
        requestBody.put("payload", payload);

        try {
            String response = restClient.post()
                    .uri("/events/trigger")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            log.info("Notification triggered successfully for subscriber: {}", subscriberId);
            return response;
        } catch (Exception e) {
            log.error("Failed to trigger notification: {}", e.getMessage());
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    public void upsertSubscriber(String subscriberId, String email, String firstName, String lastName) {
        Map<String, Object> subscriberData = new HashMap<>();
        subscriberData.put("subscriberId", subscriberId);
        subscriberData.put("email", email);
        subscriberData.put("firstName", firstName);
        subscriberData.put("lastName", lastName);

        try {
            restClient.post()
                    .uri("/subscribers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(subscriberData)
                    .retrieve()
                    .body(String.class);

            log.info("Subscriber created/updated: {}", subscriberId);
        } catch (Exception e) {
            log.error("Failed to upsert subscriber: {}", e.getMessage());
            throw new RuntimeException("Failed to create/update subscriber", e);
        }
    }
}