package com.github.frmi.dlq.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.frmi.dlq.api.data.DlqEntry;

public class DlqRecordDto {
    @JsonProperty(required = true)
    private String exception;
    @JsonProperty(required = true)
    private DlqEntry entry;

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
}
