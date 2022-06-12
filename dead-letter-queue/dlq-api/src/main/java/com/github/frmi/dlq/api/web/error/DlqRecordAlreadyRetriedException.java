package com.github.frmi.dlq.api.web.error;

public class DlqRecordAlreadyRetriedException extends RuntimeException {
    public DlqRecordAlreadyRetriedException(Long id) {
        super("Record with id " + id + " has already been retried. Set force parameter to true if you want to retry it again.");
    }
}
