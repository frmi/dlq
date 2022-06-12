package com.github.frmi.sample.kafka.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frmi.sample.kafka.model.Greeting;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value("${kafka.groupId}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Greeting> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(Greeting.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Greeting> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Greeting> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        factory.setCommonErrorHandler(new CommonErrorHandler() {
            @Override
            public boolean remainingRecords() {
                return false;
            }

            @Override
            public boolean deliveryAttemptHeader() {
                return false;
            }

            @Override
            public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
                LOGGER.error("Error thrown during poll for consumer " + consumer.groupMetadata().toString(), thrownException);
                if (thrownException instanceof RecordDeserializationException) {

                    // TODO: Maybe push message to DLQ - But how? Record cannot be fetched

                    RecordDeserializationException recordDeserializationException = (RecordDeserializationException) thrownException;
                    long currentOffset = recordDeserializationException.offset();
                    consumer.seek(recordDeserializationException.topicPartition(), currentOffset+1);
                    LOGGER.error("Skipping record with offset " + currentOffset + ". ConsumerGroup: " + consumer.groupMetadata().toString(), thrownException);
                }
            }

            @Override
            public void handleRecord(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
                RestTemplate restTemplate = new RestTemplate();
                DlqRecordDto recordDto = new DlqRecordDto();

                StringWriter writer = new StringWriter();
                PrintWriter printer = new PrintWriter(writer);
                thrownException.printStackTrace(printer);
                recordDto.setException(writer.toString());

                try {
                    ObjectMapper mapper = new ObjectMapper();

                    DlqEntry entry = new DlqEntry();
                    entry.setKey((String)record.key());
                    entry.setOffset(record.offset());
                    entry.setPartition(record.partition());
                    entry.setTimestamp(record.timestamp());
                    entry.setTopic(record.topic());
                    entry.setValue(mapper.writeValueAsString(record.value()));
                    recordDto.setMessage(mapper.writeValueAsString(entry));
                    ResponseEntity<DlqRecord> response = restTemplate.postForEntity("http://localhost:8080/dlq/push", recordDto, DlqRecord.class);
                    if (response.getStatusCode() == HttpStatus.OK) {
                        LOGGER.error("Error thrown during handling of record " + record + ". Persisted as " + response, thrownException);
                    } else {
                        LOGGER.error("Error pushing record " + record + ". Response " + response);
                    }

                } catch (JsonProcessingException e) {
                    LOGGER.error("Error serializing value of record " + record, e);
                }
            }

            @Override
            public void handleRemaining(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
                throw new UnsupportedOperationException("This operation is not supported. This error handler is not handling remaining records");
            }

            @Override
            public void handleBatch(Exception thrownException, ConsumerRecords<?, ?> data, Consumer<?, ?> consumer, MessageListenerContainer container, Runnable invokeListener) {
                throw new UnsupportedOperationException("This operation is not supported. This error handler is not handling batches");
            }

            @Override
            public int deliveryAttempt(TopicPartitionOffset topicPartitionOffset) {
                return 0;
            }

            @Override
            public void clearThreadState() {
                CommonErrorHandler.super.clearThreadState();
            }

            @Override
            public boolean isAckAfterHandle() {
                return CommonErrorHandler.super.isAckAfterHandle();
            }

            @Override
            public void setAckAfterHandle(boolean ack) {
                CommonErrorHandler.super.setAckAfterHandle(ack);
            }
        });

        return factory;
    }


    public static Map<String, byte[]> headersToMap(Headers headers) {
        Map<String, byte[]> headerMap = new HashMap<>();
        headers.forEach(header -> headerMap.put(header.key(), header.value()));
        return headerMap;
    }

}