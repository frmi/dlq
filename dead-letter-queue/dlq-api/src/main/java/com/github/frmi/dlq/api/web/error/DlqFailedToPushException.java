package com.github.frmi.dlq.api.web.error;

import com.github.frmi.dlq.api.web.dto.DlqRecordDto;

public class DlqFailedToPushException extends RuntimeException {
    public DlqFailedToPushException(DlqRecordDto dto) {
        super("Failed to push record. " + dto);
    }
}
