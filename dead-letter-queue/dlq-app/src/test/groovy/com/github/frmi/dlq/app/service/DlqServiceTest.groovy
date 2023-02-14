package com.github.frmi.dlq.app.service

import com.github.frmi.dlq.api.data.DlqEntry
import com.github.frmi.dlq.api.dto.DlqRecordDto
import com.github.frmi.dlq.api.dto.DlqRecordDtoResponse
import com.github.frmi.dlq.app.DlqTestApplication
import com.github.frmi.dlq.app.DqlRetryMockConfig
import com.github.frmi.dlq.app.error.DlqRecordNotFoundException
import com.github.frmi.dlq.app.error.DlqRetryFailedException
import com.github.frmi.dlq.data.DlqRecord
import com.github.frmi.dlq.data.DlqRecordRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootTest(classes = [DlqTestApplication, DlqService, DlqRecordRepository, DqlRetryMockConfig])
class DlqServiceTest extends Specification {

    @Autowired
    private DlqService dlqService
    @Autowired
    private DlqRecordRepository repository

    @Autowired
    @Qualifier("dlqRetryMock")
    private DlqRetry retry

    void setup() {
        DlqRecord record1 = new DlqRecord()
        record1.setEntry(createDlqEntry())
        record1.setException("Exception")
        repository.save(record1)
        DlqRecord record2 = new DlqRecord()
        record2.setEntry(createDlqEntry())
        record2.setException("Exception")
        record2.setDequeued(true)
        repository.save(record2)
    }

    void cleanup() {
        repository.deleteAll()
    }

    @Unroll
    def "GetDlqRecords"() {

        when:
        List<DlqRecordDtoResponse> records = dlqService.getDlqRecords(includeRetried)
        then:
        records.size() == expectedCount

        where:
        includeRetried | expectedCount
        false          | 1
        true           | 2
    }

    def "FindById"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setEntry(createDlqEntry())
        record.setException("Exception")
        record = repository.save(record)

        when:
        DlqRecordDtoResponse response = dlqService.findById(record.getId())
        then:
        response.getId() == record.getId()
    }

    def "FindById - not found"() {

        when:
        dlqService.findById(Long.MAX_VALUE)

        then:
        thrown(DlqRecordNotFoundException)
    }

    def "Push"() {
        given:
        DlqRecordDto recordDto = new DlqRecordDto()
        recordDto.setEntry(createDlqEntry())
        recordDto.setException("Exception")

        when:
        DlqRecordDtoResponse response = dlqService.push(recordDto, null)
        then:
        noExceptionThrown()
        response.getId() != null
        repository.findById(response.getId()) != null
    }

    def "Retry - successful retry"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setEntry(createDlqEntry())
        record.setException("Exception")

        when:
        record = repository.save(record)
        retry.retry(_ as DlqRecord) >> true

        then:"Record is not yet retried / dequeued"
        record.getId() != null
        !record.isDequeued()
        record.getDequeuedAt() == null

        when:
        DlqRecordDtoResponse response = dlqService.retry(record.getId())

        then: "Record is now retried / dequeued"
        noExceptionThrown()
        response.isDequeued()
        response.getDequeuedAt() != null
        response.getDequeuedAt().isAfter(record.getCreatedAt())
        response.getDequeuedAt().isBefore(LocalDateTime.now())
    }

    def "Retry - unsuccessful retry"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setEntry(createDlqEntry())
        record.setException("Exception")

        when:
        record = repository.save(record)
        retry.retry(_ as DlqRecord) >> false

        then:"Record is not yet retried / dequeued"
        record.getId() != null
        !record.isDequeued()

        when:
        dlqService.retry(record.getId())
        record = repository.findById(record.getId()).get()

        then: "Record is now retried / dequeued"
        thrown(DlqRetryFailedException)
        !record.isDequeued()
        record.getDequeuedAt() == null
    }

    DlqEntry createDlqEntry() {
        def entry = new DlqEntry()
        entry.setKey("key")
        entry.setOffset(1)
        entry.setPartition(1)
        entry.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        entry.setTopic("dummy.topic")
        entry.setValue("{}")
        return entry
    }

}
