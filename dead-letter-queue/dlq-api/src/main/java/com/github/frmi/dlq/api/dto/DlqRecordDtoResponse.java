package com.github.frmi.dlq.api.dto;

import com.github.frmi.dlq.api.data.DlqEntry;

import java.time.LocalDateTime;

public class DlqRecordDtoResponse {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int retryCount;
    private LocalDateTime dequeuedAt;
    private DlqEntry entry;
    private String exception;
    private boolean dequeued;

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

    public void setEntry(DlqEntry entry) {
        this.entry = entry;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getDequeuedAt() {
        return dequeuedAt;
    }

    public void setDequeuedAt(LocalDateTime dequeuedAt) {
        this.dequeuedAt = dequeuedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDequeued() {
        return dequeued;
    }

    public void setDequeued(boolean dequeued) {
        this.dequeued = dequeued;
    }
}
