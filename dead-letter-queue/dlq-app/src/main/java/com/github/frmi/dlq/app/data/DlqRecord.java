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
     * Depicts whether the record has successfully been retried.
     */
    private boolean dequeued;

    /**
     * Record created at.
     */
    private LocalDateTime dequeuedAt;

    /**
     * Content of the message that holds everything for the user to retry this record.
     */
    @Convert(converter = DlqEntryConverter.class)
    @Column(nullable = false, length = 10000)
    private DlqEntry entry;

    /**
     * The exception that caused this record to be sent to dlq.
     */
    @Lob
    private String exception;

    public DlqRecord() {
        this.createdAt = LocalDateTime.now();
        this.dequeued = false;
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

    public boolean isDequeued() {
        return dequeued;
    }

    public void setDequeued(boolean dequeued) {
        this.dequeued = dequeued;
    }

    public LocalDateTime getDequeuedAt() {
        return dequeuedAt;
    }

    public void setDequeuedAt(LocalDateTime dequeuedAt) {
        this.dequeuedAt = dequeuedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DlqRecord dlqRecord = (DlqRecord) o;
        return dequeued == dlqRecord.dequeued && Objects.equals(id, dlqRecord.id) && Objects.equals(createdAt, dlqRecord.createdAt) && Objects.equals(entry, dlqRecord.entry) && Objects.equals(exception, dlqRecord.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, dequeued, entry, exception);
    }

    @Override
    public String toString() {
        return "DlqRecord{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", dequeued=" + dequeued +
                ", message='" + entry + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
