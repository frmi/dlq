package com.github.frmi.dlq.api.web;

import com.github.frmi.dlq.api.service.DlqRetryer;
import com.github.frmi.dlq.api.util.DlqMapper;
import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import com.github.frmi.dlq.api.web.error.DlqRecordNotFoundException;
import com.github.frmi.dlq.api.web.error.DlqRetryFailedException;
import com.github.frmi.dlq.api.data.DlqRecord;
import com.github.frmi.dlq.api.data.DlqRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeadLetterQueueController {

    private final DlqRetryer retryer;
    private final DlqRecordRepository repository;

    public DeadLetterQueueController(DlqRetryer retryer, DlqRecordRepository repository) {
        this.retryer = retryer;
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<DlqRecord> all() {
        return repository.findAll();
    }

    @GetMapping("/find/{id}")
    public DlqRecord find(@PathVariable long id) {
        return repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));
    }

    @PostMapping("/push")
    public DlqRecord push(@RequestBody DlqRecordDto dlqRecordDto) {
        DlqRecord record = DlqMapper.dtoToEntity(dlqRecordDto);
        return repository.save(record);
    }

    @GetMapping("/retry/{id}")
    public ResponseEntity<DlqRecord> retry(@PathVariable long id) {
        DlqRecord record = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));

        boolean result = retryer.retry(record);
        if (result) {
            record.setDequeued(true);
            record = repository.save(record);
            return ResponseEntity.ok(record);
        }

        throw new DlqRetryFailedException(id);
    }
}
