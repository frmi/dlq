package com.github.frmi.dlq.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.frmi.dlq.api.service.DlqRetryer;
import com.github.frmi.dlq.app.kafka.KafkaRetryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class DlqConfig {

    @Bean
    public DlqRetryer retryer(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaRetryer(kafkaTemplate);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
