package com.github.frmi.dlq.app.util;

import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.data.DlqRecord;

public class DlqMapper {

    private DlqMapper() {
        // Util class
    }

    public static DlqRecord dtoToEntity(DlqRecordDto dto) {
        DlqRecord dlqRecord = new DlqRecord();
        dlqRecord.setException(dto.getException());
        dlqRecord.setEntry(dto.getEntry());
        return dlqRecord;
    }

    public static DlqRecordDtoResponse recordToResponseEntity(DlqRecord dlqRecord) {
        DlqRecordDtoResponse response = new DlqRecordDtoResponse();
        response.setException(dlqRecord.getException());
        response.setEntry(dlqRecord.getEntry());
        response.setRetryCount(dlqRecord.getRetryCount());
        response.setId(dlqRecord.getId());
        response.setCreatedAt(dlqRecord.getCreatedAt());
        response.setUpdatedAt(dlqRecord.getUpdatedAt());
        response.setDequeuedAt(dlqRecord.getDequeuedAt());
        response.setDequeued(dlqRecord.isDequeued());
        return response;
    }

}
