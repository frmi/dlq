package com.github.frmi.dlq.api.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DeadLetterQueueAdvice {

    @ResponseBody
    @ExceptionHandler(DlqRecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String recordNotFoundHandler(DlqRecordNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DlqRetryFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String retryFailedHandler(DlqRetryFailedException ex) {
        return ex.getMessage();
    }

}