package com.github.frmi.dlq.app.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class DeadLetterQueueAdvice {

    @ResponseBody
    @ExceptionHandler(DlqRecordNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    String recordNotFoundHandler(DlqRecordNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DlqRetryFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String retryFailedHandler(DlqRetryFailedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DlqRecordAlreadyRetriedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String alreadyRetriedHandler(DlqRecordAlreadyRetriedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(DlqFailedToPushException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String failedToPushHandler(DlqFailedToPushException ex) {
        return ex.getMessage();
    }

}