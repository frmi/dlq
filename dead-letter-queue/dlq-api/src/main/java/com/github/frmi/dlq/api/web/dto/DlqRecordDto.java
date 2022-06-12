package com.github.frmi.dlq.api.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DlqRecordDto {
    @JsonProperty(required = true)
    private String exception;
    @JsonProperty(required = true)
    private String message;

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
}
