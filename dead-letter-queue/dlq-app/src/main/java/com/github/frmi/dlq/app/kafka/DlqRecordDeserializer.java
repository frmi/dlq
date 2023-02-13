package com.github.frmi.dlq.app.kafka;


import com.github.frmi.dlq.api.dto.DlqRecordDto;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class DlqRecordDeserializer extends JsonDeserializer<DlqRecordDto> {



}
