package com.github.frmi.dlq.app.service;

import com.github.frmi.dlq.app.data.DlqRecord;
import com.github.frmi.dlq.app.data.DlqRecordRepository;
import com.github.frmi.dlq.app.util.DlqMapper;
import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.app.error.DlqFailedToPushException;
import com.github.frmi.dlq.app.error.DlqRecordAlreadyRetriedException;
import com.github.frmi.dlq.app.error.DlqRecordNotFoundException;
import com.github.frmi.dlq.app.error.DlqRetryFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DlqService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DlqService.class);

    private final DlqRecordRepository repository;
    private final DlqRetry retry;

    public DlqService(DlqRecordRepository repository, DlqRetry retry) {
        this.repository = repository;
        this.retry = retry;
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
            LOGGER.info("Successfully persisted DlqRecord. " + record.getEntry());
            return DlqMapper.recordToResponseEntity(record);
        }

        LOGGER.error("Failed to persist DlqRecord. " + dto.getEntry());
        throw new DlqFailedToPushException(dto);
    }

    public DlqRecordDtoResponse retry(long id, boolean force) {
        DlqRecord record = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));

        if (record.isDequeued() && !force) {
            throw new DlqRecordAlreadyRetriedException(id);
        } else if (record.isDequeued() && force) {
            LOGGER.warn("Retrying already retried record. " + record);
        }

        try {
            boolean result = retry.retry(record);
            if (result) {
                record.setDequeued(true);
                record.setDequeuedAt(LocalDateTime.now());
                record = repository.save(record);
                return DlqMapper.recordToResponseEntity(record);
            }
        } catch (Exception e) {
            LOGGER.error("Exception caught while retrying record. " + record, e);
        }

        throw new DlqRetryFailedException(id);
    }

}
