package com.github.frmi.dlq.api.data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class DlqRecord {

    private @Id @GeneratedValue Long id;
    private LocalDateTime createdAt;
    private String topic;
    private int partition;
    private long timestamp;
    private boolean dequeued;

    @Lob
    private String exception;

    @Lob
    private Object headers;

    @Column(name="`value`")
    @Lob
    private String value;

    @Column(name="`key`")
    private String key;

    @Column(name="`offset`")
    private long offset;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDequeued() {
        return dequeued;
    }

    public void setDequeued(boolean dequeued) {
        this.dequeued = dequeued;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DlqRecord dlqRecord = (DlqRecord) o;
        return offset == dlqRecord.offset && partition == dlqRecord.partition && timestamp == dlqRecord.timestamp && dequeued == dlqRecord.dequeued && Objects.equals(id, dlqRecord.id) && Objects.equals(createdAt, dlqRecord.createdAt) && Objects.equals(value, dlqRecord.value) && Objects.equals(exception, dlqRecord.exception) && Objects.equals(key, dlqRecord.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, value, exception, key, offset, partition, timestamp, dequeued);
    }

    @Override
    public String toString() {
        return "DlqRecord{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", value='" + value + '\'' +
                ", exception='" + exception + '\'' +
                ", key='" + key + '\'' +
                ", offset=" + offset +
                ", partition=" + partition +
                ", timestamp=" + timestamp +
                ", dequeued=" + dequeued +
                '}';
    }
}
