package com.github.frmi.dlq.api.kafka;

import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class DlqRecordSerializer extends JsonSerializer<DlqRecordDto> {



}
