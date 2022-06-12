package com.github.frmi.dlq.api.util;

import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import com.github.frmi.dlq.api.data.DlqRecord;

import java.util.Map;

public class DlqMapper {

    private DlqMapper() {
        // Util class
    }

    public static DlqRecord dtoToEntity(DlqRecordDto dto) {
        DlqRecord dlqRecord = new DlqRecord();
        dlqRecord.setException(dto.getException());
        dlqRecord.setValue(dto.getValue());
        dlqRecord.setKey(dto.getKey());
        dlqRecord.setOffset(dto.getOffset());
        dlqRecord.setTopic(dto.getTopic());
        dlqRecord.setPartition(dto.getPartition());
        dlqRecord.setTimestamp(dto.getTimestamp());
        return dlqRecord;
    }

}
