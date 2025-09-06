package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.payments.providers.mpesa.dtos.MpesaResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MpesaEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MpesaEventConsumer.class);

    private Mpesa mpesa;

    @KafkaListener(
            topics = "mpesa.callback",
            groupId = "mpesa-callback-group",
            containerFactory = "mpesaCallBackKafkaListenerFactory"
    )
    public void handleCallBackEvent(MpesaResponse response) {
        logger.info("Consumed Mpesa Callback: {}", response);
        mpesa.callback(response);
    }
}
