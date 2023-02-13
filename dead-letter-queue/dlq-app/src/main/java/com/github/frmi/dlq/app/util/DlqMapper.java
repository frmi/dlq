package com.github.frmi.dlq.app.util;

import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.app.data.DlqRecord;

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

    public static DlqRecordDtoResponse recordToResponseEntity(DlqRecord record) {
        DlqRecordDtoResponse response = new DlqRecordDtoResponse();
        response.setException(record.getException());
        response.setEntry(record.getEntry());
        response.setRetryCount(record.getRetryCount());
        response.setId(record.getId());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        response.setDequeuedAt(record.getDequeuedAt());
        return response;
    }

}
