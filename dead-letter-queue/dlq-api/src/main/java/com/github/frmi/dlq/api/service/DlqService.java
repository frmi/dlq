package com.github.frmi.dlq.api.service;

import com.github.frmi.dlq.api.data.DlqRecord;
import com.github.frmi.dlq.api.data.DlqRecordRepository;
import com.github.frmi.dlq.api.util.DlqMapper;
import com.github.frmi.dlq.api.web.dto.DlqRecordDto;
import com.github.frmi.dlq.api.web.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.api.web.error.DlQRecordAlreadyRetriedException;
import com.github.frmi.dlq.api.web.error.DlqRecordNotFoundException;
import com.github.frmi.dlq.api.web.error.DlqRetryFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DlqService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DlqService.class);

    private final DlqRecordRepository repository;
    private final DlqRetryer retryer;

    public DlqService(DlqRecordRepository repository, DlqRetryer retryer) {
        this.repository = repository;
        this.retryer = retryer;
    }

    public List<DlqRecordDtoResponse> getDlqRecords(boolean includeRetried) {
        if (!includeRetried) {
            DlqRecord probe = new DlqRecord();
            probe.setDequeued(false);
            probe.setCreatedAt(null);
            return repository.findAll(Example.of(probe), Sort.by("createdAt")).stream().map(DlqMapper::recordToResponseEntity).collect(Collectors.toList());
        }

        return repository.findAll().stream().map(DlqMapper::recordToResponseEntity).collect(Collectors.toList());
    }

    public DlqRecordDtoResponse findById(long id) {
        DlqRecord dlqRecord = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));
        return DlqMapper.recordToResponseEntity(dlqRecord);
    }

    public DlqRecordDtoResponse push(DlqRecordDto dto) {
        DlqRecord record = DlqMapper.dtoToEntity(dto);

        record = repository.save(record);
        if (record.getId() != null) {
            LOGGER.info("Successfully persisted DlqRecord " + record);
        } else {
            LOGGER.error("Failed to persist DlqRecord " + record);
        }

        return DlqMapper.recordToResponseEntity(record);

    }

    public ResponseEntity<DlqRecordDtoResponse> retry(long id, boolean force) {
        DlqRecord record = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));

        if (record.isDequeued() && !force) {
            throw new DlQRecordAlreadyRetriedException(id);
        } else if (record.isDequeued() && force) {
            LOGGER.warn("Retrying already retried record. " + record);
        }

        boolean result = retryer.retry(record);
        if (result) {
            record.setDequeued(true);
            record = repository.save(record);
            return ResponseEntity.ok(DlqMapper.recordToResponseEntity(record));
        }

        throw new DlqRetryFailedException(id);
    }

}