package com.github.frmi.dlq.api.data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class DlqRecord {

    private @Id @GeneratedValue Long id;
    private LocalDateTime createdAt;
    private boolean dequeued;

    @Lob
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDequeued() {
        return dequeued;
    }

    public void setDequeued(boolean dequeued) {
        this.dequeued = dequeued;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DlqRecord dlqRecord = (DlqRecord) o;
        return dequeued == dlqRecord.dequeued && Objects.equals(id, dlqRecord.id) && Objects.equals(createdAt, dlqRecord.createdAt) && Objects.equals(message, dlqRecord.message) && Objects.equals(exception, dlqRecord.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, dequeued, message, exception);
    }

    @Override
    public String toString() {
        return "DlqRecord{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", dequeued=" + dequeued +
                ", message='" + message + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
