package com.github.frmi.dlq.api.data;

import java.util.Objects;

public class DlqEntry {
    private String topic;
    private int partition;
    private long timestamp;
    private String value;
    private String key;
    private long offset;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DlqEntry dlqEntry = (DlqEntry) o;
        return partition == dlqEntry.partition && timestamp == dlqEntry.timestamp && offset == dlqEntry.offset && topic.equals(dlqEntry.topic) && value.equals(dlqEntry.value) && Objects.equals(key, dlqEntry.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, timestamp, value, key, offset);
    }

    @Override
    public String toString() {
        return "DlqEntry{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", timestamp=" + timestamp +
                ", value='" + value + '\'' +
                ", key='" + key + '\'' +
                ", offset=" + offset +
                '}';
    }
}
