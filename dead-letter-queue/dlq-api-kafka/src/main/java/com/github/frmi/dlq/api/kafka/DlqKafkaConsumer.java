package com.github.frmi.dlq.api.kafka;

import com.github.frmi.dlq.api.service.DlqService;
import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@EnableKafka
@Component
public class DlqKafkaConsumer {

    private final DlqService dlqService;

    public DlqKafkaConsumer(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    @KafkaListener(topics = "${dlq.topic.queue}", groupId = "deadletterqueue")
    public void listenDeadLetterQueue(DlqRecordDto dlqRecordDto) {
        dlqService.push(dlqRecordDto);
    }

}
