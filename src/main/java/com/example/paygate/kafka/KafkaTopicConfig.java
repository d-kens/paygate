package com.example.paygate.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic mpesaCallBackTopic() {
        return TopicBuilder.name("mpesa.callback").build();
    }

    @Bean
    public NewTopic merchantWebhook() {
        return TopicBuilder.name("merchant.webhook").build();
    }
}
