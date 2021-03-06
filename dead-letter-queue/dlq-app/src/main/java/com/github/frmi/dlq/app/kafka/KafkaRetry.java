package com.github.frmi.dlq.app.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frmi.dlq.api.data.DlqRecord;
import com.github.frmi.dlq.api.service.DlqRetry;
import com.github.frmi.dlq.app.DlqEntry;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaRetry implements DlqRetry {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRetry.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public KafkaRetry(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean retry(DlqRecord record) {

        try {
            DlqEntry entry = objectMapper.readValue(record.getMessage(), DlqEntry.class);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(entry.getTopic(), entry.getPartition(), entry.getKey(), entry.getValue());
            kafkaTemplate.send(producerRecord);
            LOGGER.info("Record has been retried. " + record);
            return true;
        } catch (Exception e) {
            LOGGER.error("Record could not be retried. " + record, e);
        }

        return false;
    }

}
