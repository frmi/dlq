package com.github.frmi.dlq.api.web.error;

public class DlqRetryFailedException extends RuntimeException {
    public DlqRetryFailedException(Long id) {
        super("Retry failed for record with id " + id);
    }
}
