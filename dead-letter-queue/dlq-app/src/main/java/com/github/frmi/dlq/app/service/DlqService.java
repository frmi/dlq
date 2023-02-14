package com.github.frmi.dlq.app.service;

import com.github.frmi.dlq.api.dto.DlqRecordDto;
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse;
import com.github.frmi.dlq.app.error.DlqFailedToPersistException;
import com.github.frmi.dlq.app.error.DlqRecordNotFoundException;
import com.github.frmi.dlq.app.error.DlqRetryFailedException;
import com.github.frmi.dlq.app.util.DlqMapper;
import com.github.frmi.dlq.data.DlqRecord;
import com.github.frmi.dlq.data.DlqRecordRepository;
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
            probe.setDequeued(false);
            probe.setCreatedAt(null);
            probe.setUpdatedAt(null);
            return repository.findAll(Example.of(probe), Sort.by("createdAt")).stream().map(DlqMapper::recordToResponseEntity).collect(Collectors.toList());
        }

        return repository.findAll(Sort.by("createdAt")).stream().map(DlqMapper::recordToResponseEntity).collect(Collectors.toList());
    }

    public DlqRecordDtoResponse findById(long id) {
        DlqRecord dlqRecord = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));
        return DlqMapper.recordToResponseEntity(dlqRecord);
    }

    public DlqRecordDtoResponse push(DlqRecordDto dto, Long dlqId) {

        DlqRecord dlqRecord;
        if (dlqId != null) {
            Optional<DlqRecord> recordOpt = repository.findById(dlqId);
            dlqRecord = recordOpt.orElseGet(() -> DlqMapper.dtoToEntity(dto));
            dlqRecord.setUpdatedAt(LocalDateTime.now());
            dlqRecord.setDequeued(false);
        } else {
            dlqRecord = DlqMapper.dtoToEntity(dto);
        }

        try {
            dlqRecord = repository.save(dlqRecord);
            LOGGER.info("Successfully persisted DlqRecord with id '{}'", dlqRecord.getId());
            return DlqMapper.recordToResponseEntity(dlqRecord);
        } catch (Exception e) {
            throw new DlqFailedToPersistException(dto, e);
        }
    }

    public DlqRecordDtoResponse retry(long id) {
        DlqRecord dlqRecord = repository.findById(id).orElseThrow(() -> new DlqRecordNotFoundException(id));

        try {
            boolean result = retry.retry(dlqRecord);
            if (result) {
                dlqRecord.setRetryCount(dlqRecord.getRetryCount() + 1);
                LocalDateTime retriedAt = LocalDateTime.now();
                dlqRecord.setUpdatedAt(retriedAt);
                dlqRecord.setDequeuedAt(retriedAt);
                dlqRecord.setDequeued(true);
                dlqRecord = repository.save(dlqRecord);
                return DlqMapper.recordToResponseEntity(dlqRecord);
            }
        } catch (Exception e) {
            LOGGER.error("Exception caught while retrying dlqRecord. " + dlqRecord, e);
        }

        throw new DlqRetryFailedException(id);
    }

}
