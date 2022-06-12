package com.github.frmi.dlq.api.web.error;

public class DlqRecordNotFoundException extends RuntimeException {
    public DlqRecordNotFoundException(Long id) {
        super("Could not find record with id=" + id);
    }
}
