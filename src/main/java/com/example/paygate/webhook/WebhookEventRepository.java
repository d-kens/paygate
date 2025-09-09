package com.example.paygate.webhook;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    List<WebhookEvent> findTop100ByStatusAndNextAttemptAtBeforeOrderByNextAttemptAtAsc(String status, OffsetDateTime when);
    Optional<WebhookEvent> findByEventId(String eventId);
}
