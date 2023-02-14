package com.github.frmi.dlq.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.frmi.dlq.app.kafka.KafkaRetry;
import com.github.frmi.dlq.app.service.DlqRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class DlqConfig {

    @Bean
    public DlqRetry retryer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                            @Value("${dlq.topic.retry.postfix:#{null}}") String postfixTopic) {
        return new KafkaRetry(kafkaTemplate, objectMapper, postfixTopic);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
