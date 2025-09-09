package com.example.paygate.webhook;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.OffsetDateTime;


@Service
@AllArgsConstructor
public class WebhookEventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(WebhookEventDispatcher.class);

    private final WebhookEventRepository webhookEventRepository;
    private final BackoffConfig backoffConfig;


    @KafkaListener(
            topics = "webhook.dispatch",
            groupId = "webhook-dispatcher"
    )
    public void handleDispatch(String eventId) {
        WebhookEvent event = webhookEventRepository.findByEventId(eventId).orElse(null);


        if (event == null) {
            logger.error("Received dispatch for unknown eventId={}", eventId);
            return;
        }

        // Only attempt if status is PENDING
        if (!"PENDING".equals(event.getStatus())) {
            logger.warn("Event {} in status {} — skipping", eventId, event.getStatus());
            return;
        }

        try {

            RestClient restClient = RestClient.builder()
                    .baseUrl(event.getUrl())
                    .build();

            var response = restClient.post()
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(event.getPayload())
                    .retrieve()
                    .toBodilessEntity();

            event.setStatus("DELIVERED");
            event.setUpdatedAt(OffsetDateTime.now());
            event.setLastError(null);
            webhookEventRepository.save(event);

            logger.info("Delivered webhook event {} to {}", eventId, event.getUrl());

        } catch (RestClientResponseException ex) {
            // remote returned non-2xx
            logger.error("Webhook delivery failed for event {} status={} body={}", eventId, ex.getRawStatusCode(), ex.getResponseBodyAsString());
            handleFailure(event, ex.getMessage());
        }
        catch (RestClientException ex) {
            logger.error("Webhook delivery network error for event {}: {}", eventId, ex.getMessage());
            handleFailure(event, ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error delivering event {}", eventId, ex);
            handleFailure(event, ex.getMessage());
        }
    }

    private void handleFailure(WebhookEvent event, String error) {
        int attempts = event.getAttempts() + 1;
        event.setAttempts(attempts);
        event.setLastError(error);
        OffsetDateTime nextAttempt = computeNextAttempt(attempts, backoffConfig);
        event.setNextAttemptAt(nextAttempt);
        event.setUpdatedAt(OffsetDateTime.now());

        if (attempts >= backoffConfig.getMaxAttempts()) {
            event.setStatus("PERMANENTLY_FAILED");
            webhookEventRepository.save(event);
            logger.warn("Event {} permanently failed after {} attempts", event.getEventId(), attempts);
            return;
        }

        webhookEventRepository.save(event);
    }

    private OffsetDateTime computeNextAttempt(int attempts, BackoffConfig config) {
        // attempts is 1-based (1 => first retry)
        int index = Math.min(attempts - 1, backoffConfig.getMaxAttempts() - 1);
        int seconds = backoffConfig.getBackoffSeconds()[index];
        return OffsetDateTime.now().plusSeconds(seconds);
    }
}
