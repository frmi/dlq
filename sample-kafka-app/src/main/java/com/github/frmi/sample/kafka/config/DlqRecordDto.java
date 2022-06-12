package com.github.frmi.sample.kafka.config;

public class DlqRecordDto {
    private String exception;
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
