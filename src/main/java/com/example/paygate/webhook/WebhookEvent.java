package com.example.paygate.webhook;


import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;


@Getter
@Setter
@Entity
@Builder
@Table(name = "webhook_events")
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_id")
    private String eventId;

    @Column(name="transaction_id")
    private Long transactionId;

    @Column(name="merchant_id")
    private Long merchantId;

    @Column(name="url")
    private String url;

    @Column(name="payload")
    private String payload;

    @Column(name="status")
    private String status; // PENDING, DELIVERED, PERMANENTLY_FAILED

    @Column(name="attempts")
    private Integer attempts;

    @Column(name="next_attempt_at")
    private OffsetDateTime nextAttemptAt;

    @Column(name="last_error")
    private String lastError;

    @Column(name="created_at")
    private OffsetDateTime createdAt;

    @Column(name="updated_at")
    private OffsetDateTime updatedAt;
}