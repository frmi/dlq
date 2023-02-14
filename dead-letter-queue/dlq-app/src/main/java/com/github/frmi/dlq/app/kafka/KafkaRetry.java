package com.github.frmi.dlq.app.kafka;

import com.github.frmi.dlq.api.DlqHeaders;
import com.github.frmi.dlq.api.data.DlqEntry;
import com.github.frmi.dlq.app.service.DlqRetry;
import com.github.frmi.dlq.data.DlqRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;

public class KafkaRetry implements DlqRetry {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRetry.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String retryTopicPostFix;

    public KafkaRetry(KafkaTemplate<String, String> kafkaTemplate, String retryTopicPostFix) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTopicPostFix = retryTopicPostFix;
    }

    @Override
    public boolean retry(DlqRecord dlqRecord) {
        DlqEntry entry = dlqRecord.getEntry();
        String topic = getRetryTopic(entry.getTopic());

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, entry.getPartition(), entry.getKey(), entry.getValue());
        producerRecord.headers().add(DlqHeaders.DLQ_ID, String.valueOf(dlqRecord.getId()).getBytes(StandardCharsets.UTF_8));

        try {
            kafkaTemplate.send(producerRecord);
            LOGGER.info("Record with id '{}' has been requeued on topic '{}'", dlqRecord.getId(), topic);
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("Record with id '%s' could not be retried on topic '%s'", dlqRecord.getId(), topic), e);
        }

        return false;
    }

    private String getRetryTopic(String topic) {
        if (retryTopicPostFix == null || retryTopicPostFix.isBlank()) {
            return topic;
        }

        return String.format("%s.%s", topic, retryTopicPostFix);
    }

}
