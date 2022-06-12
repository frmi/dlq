package com.github.frmi.dlq.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.frmi.dlq.api.EnableDeadLetterQueue;
import com.github.frmi.dlq.api.service.DlqRetry;
import com.github.frmi.dlq.app.kafka.KafkaRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@EnableDeadLetterQueue
@Configuration
public class DlqConfig {

    @Bean
    public DlqRetry retryer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new KafkaRetry(kafkaTemplate, objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
