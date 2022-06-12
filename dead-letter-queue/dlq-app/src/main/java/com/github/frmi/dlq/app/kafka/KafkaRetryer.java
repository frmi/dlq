package com.github.frmi.dlq.app.kafka;

import com.github.frmi.dlq.api.service.DlqRetryer;
import com.github.frmi.dlq.api.data.DlqRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

public class KafkaRetryer implements DlqRetryer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRetryer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaRetryer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public boolean retry(DlqRecord record) {

        try {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(record.getTopic(), record.getPartition(), record.getKey(), record.getValue());
            kafkaTemplate.send(producerRecord);
            LOGGER.info("Record has been retried. " + record);
            return true;
        } catch (Exception e) {
            LOGGER.error("Record could not be retried. " + record, e);
        }

        return false;
    }

}
