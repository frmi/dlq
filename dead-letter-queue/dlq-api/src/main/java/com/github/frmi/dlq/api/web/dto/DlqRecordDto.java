package com.github.frmi.dlq.api.web.dto;

import java.util.Map;

public class DlqRecordDto {
    private String exception;
    private String key;
    private String value;
    private int partition;
    private long offset;
    private long timestamp;
    private String topic;
    private Map<String, byte[]> headers;

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

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, byte[]> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, byte[]> headers) {
        this.headers = headers;
    }
}
