package com.github.frmi.dlq.api.web;

import com.github.frmi.dlq.api.service.DlqService;
import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import com.github.frmi.dlq.api.web.dto.DlqRecordDtoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeadLetterQueueController {

    private final DlqService dlqService;

    public DeadLetterQueueController(DlqService dlqService) {
        this.dlqService = dlqService;
    }

    @GetMapping("/all")
    public List<DlqRecordDtoResponse> all(@RequestParam(required = false) boolean includeRetried) {
        return dlqService.getDlqRecords(includeRetried);
    }

    @GetMapping("/find/{id}")
    public DlqRecordDtoResponse find(@PathVariable long id) {
        return dlqService.findById(id);
    }

    @PostMapping("/push")
    public DlqRecordDtoResponse push(@RequestBody DlqRecordDto dlqRecordDto) {
        return dlqService.push(dlqRecordDto);
    }

    @GetMapping("/retry/{id}")
    public ResponseEntity<DlqRecordDtoResponse> retry(@PathVariable long id, @RequestParam(required = false) boolean forceRetry) {
        return dlqService.retry(id, forceRetry);
    }
}
