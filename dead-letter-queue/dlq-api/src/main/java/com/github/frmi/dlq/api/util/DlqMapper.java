package com.github.frmi.dlq.api.util;

import com.github.frmi.dlq.api.data.DlqRecord;
import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import com.github.frmi.dlq.api.web.dto.DlqRecordDtoResponse;

public class DlqMapper {

    private DlqMapper() {
        // Util class
    }

    public static DlqRecord dtoToEntity(DlqRecordDto dto) {
        DlqRecord dlqRecord = new DlqRecord();
        dlqRecord.setException(dto.getException());
        dlqRecord.setMessage(dto.getMessage());
        return dlqRecord;
    }

    public static DlqRecordDtoResponse recordToResponseEntity(DlqRecord record) {
        DlqRecordDtoResponse response = new DlqRecordDtoResponse();
        response.setException(record.getException());
        response.setMessage(record.getMessage());
        response.setId(record.getId());
        response.setCreatedAt(record.getCreatedAt());
        response.setDequeued(record.isDequeued());
        response.setDequeuedAt(record.getDequeuedAt());
        return response;
    }

}
