package com.github.frmi.dlq.app.error;

import com.github.frmi.dlq.api.dto.DlqRecordDto;

public class DlqFailedToPushException extends RuntimeException {
    public DlqFailedToPushException(DlqRecordDto dto, Throwable throwable) {
        super("Failed to push record. " + dto, throwable);
    }
}
