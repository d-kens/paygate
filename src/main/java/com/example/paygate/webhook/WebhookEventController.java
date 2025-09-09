package com.example.paygate.webhook;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/webhooks")
public class WebhookEventController {
    private final WebhookEventService webhookEventService;

    @PostMapping("/{eventId}/replay")
    public ResponseEntity<?> replay(@PathVariable String eventId) {
        var event = webhookEventService.replay(eventId);

        if (event == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }
}
