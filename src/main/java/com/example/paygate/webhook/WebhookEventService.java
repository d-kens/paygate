package com.example.paygate.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WebhookEventService {
    private final WebhookEventRepository webhookEventRepository;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final ObjectMapper objectMapper;


    public WebhookEvent createAndDispatch(Long transactionId, Long merchantId, String webhookUrl, Object payload) {
        try {
            String eventId = UUID.randomUUID().toString();
            String payloadJson = objectMapper.writeValueAsString(payload);

            var now = OffsetDateTime.now();

            WebhookEvent event = WebhookEvent.builder()
                    .eventId(eventId)
                    .transactionId(transactionId)
                    .merchantId(merchantId)
                    .url(webhookUrl)
                    .payload(payloadJson)
                    .status("PENDING")
                    .attempts(0)
                    .nextAttemptAt(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            
            event = webhookEventRepository.save(event);

            stringKafkaTemplate.send("webhook.dispatch", eventId);
            return event;
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist or dispatch webhook event", e);
        }
    }

    public WebhookEvent replay(String eventId) {
        var event = webhookEventRepository.findByEventId(eventId).orElse(null);

        if (event == null) return null;

        event.setStatus("PENDING");
        event.setAttempts(0);
        event.setNextAttemptAt(OffsetDateTime.now());
        event.setLastError(null);
        webhookEventRepository.save(event);

        stringKafkaTemplate.send("webhook.dispatch", eventId);

        return event;
    }
}
