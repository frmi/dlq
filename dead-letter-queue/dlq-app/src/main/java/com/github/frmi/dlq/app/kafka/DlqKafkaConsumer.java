package com.github.frmi.dlq.app.kafka;

import com.github.frmi.dlq.api.DlqHeaders;
import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.app.service.DlqService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class DlqKafkaConsumer {

    private final DlqService dlqService;

    public DlqKafkaConsumer(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    @KafkaListener(topicPattern = "${dlq.topic.queue}", containerFactory = "dlqRecordDtoKafkaListenerContainerFactory")
    public void listenDeadLetterQueue(DlqRecordDto dlqRecordDto, @Header(value = DlqHeaders.DLQ_ID, required = false) String dlqIdValue) {
        Long dlqId = null;
        if (dlqIdValue != null) {
            dlqId = Long.valueOf(dlqIdValue);
        }

        dlqService.push(dlqRecordDto, dlqId);
    }

}
