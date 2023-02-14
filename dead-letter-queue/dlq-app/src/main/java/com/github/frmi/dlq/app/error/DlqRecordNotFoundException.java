package com.github.frmi.dlq.app.error;

public class DlqRecordNotFoundException extends RuntimeException {
    public DlqRecordNotFoundException(Long id) {
        super("Could not find record with id=" + id);
    }
}
