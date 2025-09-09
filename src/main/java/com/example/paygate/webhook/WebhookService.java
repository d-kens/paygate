package com.example.paygate.webhook;

import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.merchants.MerchantService;
import com.example.paygate.transactions.Transaction;
import com.example.paygate.webhook.dtos.WebhookPayload;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final MerchantService merchantService;

    @KafkaListener(
            topics = "merchant.webhook",
            groupId = "merchant-webhook-group",
            containerFactory = "merchantWebhookKafkaListenerFactory"
    )
    public void postWebhook(Transaction transaction) {
        try {
            var merchantId = transaction.getMerchant().getId();
            var merchant = merchantService.findOne(merchantId);

            var webhookUrl = merchant.getWebhookUrl();

            var webhookPayload = new WebhookPayload(
                    transaction.getId(),
                    transaction.getStatus().toString(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    transaction.getMerchantPaymentReference()
            );


            RestClient restClient = RestClient.builder().baseUrl(webhookUrl).build();


            restClient.post()
                    .headers(httpHeaders -> {
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(webhookPayload)
                    .retrieve()
                    .toBodilessEntity();

            logger.info("Webhook posted to merchant {} at {}", merchantId, webhookUrl);


        } catch (NotFoundException exception) {
            logger.error("Merchant not found for transaction {}", transaction.getId());
        } catch (Exception exception) {
            logger.error("Failed to post webhook for transaction {}: {}", transaction.getId(), exception.getMessage(), exception);
            // TODO: Push to retry queue or DLQ
        }
    }
}
