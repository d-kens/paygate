package com.example.paygate.webhook;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class WebhookRetryScheduler {
    private final WebhookEventRepository webhookEventRepository;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    @Scheduled(cron = "/1 * * * *")
    public void enqueueDueEvents() {
        var eventsDue = webhookEventRepository.findTop100ByStatusAndNextAttemptAtBeforeOrderByNextAttemptAtAsc("PENDING", OffsetDateTime.now());

        for (WebhookEvent event : eventsDue ) {
            stringKafkaTemplate.send("webhook.dispatch", event.getEventId());
        }
    }
}
