package com.github.frmi.dlq.app.service;

import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.app.data.DlqRecord;
import com.github.frmi.dlq.app.data.DlqRecordRepository;
import com.github.frmi.dlq.app.error.DlqFailedToPushException;
import com.github.frmi.dlq.app.error.DlqRecordNotFoundException;
import com.github.frmi.dlq.app.error.DlqRetryFailedException;
import com.github.frmi.dlq.app.util.DlqMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
            probe.setCreatedAt(null);
            return repository.findAll(Example.of(probe), Sort.by("createdAt")).stream().map(DlqMapper::recordToResponseEntity).collect(Collectors.toList());
        }

        return repository.findAll().stream().map(DlqMapper::recordToResponseEntity).collect(Collectors.toList());
    }

    public DlqRecordDtoResponse findById(long id) {
        DlqRecord dlqRecord = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));
        return DlqMapper.recordToResponseEntity(dlqRecord);
    }

    public DlqRecordDtoResponse push(DlqRecordDto dto, Long dlqId) {

        DlqRecord record;
        if (dlqId != null) {
            Optional<DlqRecord> recordOpt = repository.findById(dlqId);
            record = recordOpt.orElseGet(() -> DlqMapper.dtoToEntity(dto));
            record.setUpdatedAt(LocalDateTime.now());
        } else {
            record = DlqMapper.dtoToEntity(dto);
        }

        try {
            record = repository.save(record);
            LOGGER.info("Successfully persisted DlqRecord. " + record.getEntry());
            return DlqMapper.recordToResponseEntity(record);
        } catch (Exception e) {
            LOGGER.error("Failed to persist DlqRecord. " + dto.getEntry(), e);
            throw new DlqFailedToPushException(dto, e);
        }
    }

    public DlqRecordDtoResponse retry(long id) {
        DlqRecord record = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));

        try {
            boolean result = retry.retry(record);
            if (result) {
                record.setRetryCount(record.getRetryCount() + 1);
                LocalDateTime retriedAt = LocalDateTime.now();
                record.setUpdatedAt(retriedAt);
                record.setDequeuedAt(retriedAt);
                record = repository.save(record);
                return DlqMapper.recordToResponseEntity(record);
            }
        } catch (Exception e) {
            LOGGER.error("Exception caught while retrying record. " + record, e);
        }

        throw new DlqRetryFailedException(id);
    }

}
