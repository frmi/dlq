package com.github.frmi.dlq.app.error;

import com.github.frmi.dlq.api.dto.DlqRecordDto;

public class DlqFailedToPersistException extends RuntimeException {
    public DlqFailedToPersistException(DlqRecordDto dto, Throwable throwable) {
        super(String.format("Failed to push record. %s", dto.getEntry()), throwable);
    }
}
