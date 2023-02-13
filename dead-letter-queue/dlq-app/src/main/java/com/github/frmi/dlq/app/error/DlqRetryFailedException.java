package com.github.frmi.dlq.app.error;

public class DlqRetryFailedException extends RuntimeException {
    public DlqRetryFailedException(Long id) {
        super("Retry failed for record with id " + id);
    }
}
