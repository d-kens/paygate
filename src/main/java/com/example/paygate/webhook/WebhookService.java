package com.example.paygate.webhook;


import com.example.paygate.payments.providers.mpesa.MpesaEventConsumer;
import com.example.paygate.transactions.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);


    @KafkaListener(
            topics = "merchant.webhook",
            groupId = "merchant-webhook-group",
            containerFactory = "merchantWebhookKafkaListenerFactory"
    )
    public void postWebhook(Transaction transaction) {
        logger.info("Recieved webhook Data: ", transaction);
    }




}

