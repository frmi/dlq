package com.github.frmi.dlq.data.exception;

public class DlqDataException extends RuntimeException {

    public DlqDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
