package com.github.frmi.sample.kafka.config;

import java.time.LocalDateTime;
import java.util.Objects;

public class DlqRecord {

    private Long id;
    private LocalDateTime createdAt;
    private Object value;
    private String exception;
    private String type;
    private boolean dequeued;

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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isDequeued() {
        return dequeued;
    }

    public void setDequeued(boolean dequeued) {
        this.dequeued = dequeued;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DlqRecord record = (DlqRecord) o;
        return dequeued == record.dequeued && Objects.equals(id, record.id) && Objects.equals(createdAt, record.createdAt) && Objects.equals(value, record.value) && Objects.equals(exception, record.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, value, exception, dequeued);
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", value='" + value + '\'' +
                ", exception='" + exception + '\'' +
                ", dequeued=" + dequeued +
                '}';
    }
}
