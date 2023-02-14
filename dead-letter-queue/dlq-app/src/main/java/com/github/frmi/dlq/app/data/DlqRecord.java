package com.github.frmi.dlq.app.data;

import com.github.frmi.dlq.api.data.DlqEntry;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class DlqRecord {

    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dlq_record_sequence_generator")
    @SequenceGenerator(name = "dlq_record_sequence_generator", sequenceName = "dlq_record_sequence")
    private Long id;

    /**
     * Record created at.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Record created at.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Record dequeued at.
     */
    private LocalDateTime dequeuedAt;

    /**
     * Holds whether the message has been dequeued
     */
    private boolean dequeued;

    /**
     * Retry count.
     */
    private int retryCount;

    /**
     * Content of the message that holds everything for the user to retry this record.
     */
    @Convert(converter = DlqEntryConverter.class)
    @Column(nullable = false, length = 10000)
    @Lob
    private DlqEntry entry;

    /**
     * The exception that caused this record to be sent to dlq.
     */
    @Lob
    private String exception;

    public DlqRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDequeuedAt() {
        return dequeuedAt;
    }

    public void setDequeuedAt(LocalDateTime dequeuedAt) {
        this.dequeuedAt = dequeuedAt;
    }

    public boolean isDequeued() {
        return dequeued;
    }

    public void setDequeued(boolean dequeued) {
        this.dequeued = dequeued;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public DlqEntry getEntry() {
        return entry;
    }

    public void setEntry(DlqEntry message) {
        this.entry = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DlqRecord dlqRecord = (DlqRecord) o;
        return id.equals(dlqRecord.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DlqRecord{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", dequeuedAt=" + dequeuedAt +
                ", dequeued=" + dequeued +
                ", retryCount=" + retryCount +
                ", entry=" + entry +
                ", exception='" + exception + '\'' +
                '}';
    }
}
