package com.github.frmi.dlq.app.service;

import com.github.frmi.dlq.data.DlqRecord;

/**
 * Defines how the Dead Letter Queue records should be retried.
 */
public interface DlqRetry {

    /**
     * Retry mechanism.
     *
     * @param dlqRecord The dlqRecord requested retried.
     * @return {@code true} if retried successfully; {@code false} otherwise.
     */
    boolean retry(DlqRecord dlqRecord);

}
