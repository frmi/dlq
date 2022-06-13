package com.github.frmi.dlq.api.service

import com.github.frmi.dlq.api.DlqTestApplication
import com.github.frmi.dlq.api.DqlRetryMockConfig
import com.github.frmi.dlq.api.data.DlqRecord
import com.github.frmi.dlq.api.data.DlqRecordRepository
import com.github.frmi.dlq.api.web.dto.DlqRecordDto
import com.github.frmi.dlq.api.web.dto.DlqRecordDtoResponse
import com.github.frmi.dlq.api.web.error.DlqRecordAlreadyRetriedException
import com.github.frmi.dlq.api.web.error.DlqRecordNotFoundException
import com.github.frmi.dlq.api.web.error.DlqRetryFailedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Mockito.when

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
        record1.setMessage("record1")
        record1.setException("Exception")
        repository.save(record1)
        DlqRecord record2 = new DlqRecord()
        record2.setMessage("record2")
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
        record.setMessage("record")
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
        DlqRecordDto record = new DlqRecordDto()
        record.setMessage("record")
        record.setException("Exception")

        when:
        DlqRecordDtoResponse response = dlqService.push(record)
        then:
        noExceptionThrown()
        response.getId() != null
        repository.findById(response.getId()) != null
    }

    def "Retry - successful retry"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setMessage("record")
        record.setException("Exception")

        when:
        record = repository.save(record)
        retry.retry(_ as DlqRecord) >> true

        then:"Record is not yet retried / dequeued"
        record.getId() != null
        !record.isDequeued()

        when:
        DlqRecordDtoResponse response = dlqService.retry(record.getId(), false)

        then: "Record is now retried / dequeued"
        noExceptionThrown()
        response.isDequeued()
    }

    def "Retry - unsuccessful retry"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setMessage("record")
        record.setException("Exception")

        when:
        record = repository.save(record)
        retry.retry(_ as DlqRecord) >> false

        then:"Record is not yet retried / dequeued"
        record.getId() != null
        !record.isDequeued()

        when:
        dlqService.retry(record.getId(), false)

        then: "Record is now retried / dequeued"
        thrown(DlqRetryFailedException)
    }

    def "Retry - already retried - no force"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setMessage("record")
        record.setException("Exception")
        record.setDequeued(true)

        when:
        record = repository.save(record)
        dlqService.retry(record.getId(), false)

        then: "Record is now retried / dequeued"
        thrown(DlqRecordAlreadyRetriedException)
    }

    def "Retry - already retried - with force"() {
        given:
        DlqRecord record = new DlqRecord()
        record.setMessage("record")
        record.setException("Exception")
        record.setDequeued(true)

        when:
        record = repository.save(record)
        retry.retry(_ as DlqRecord) >> true

        then:"Record is correctly persisted"
        record.getId() != null

        when: "Retry"
        DlqRecordDtoResponse response = dlqService.retry(record.getId(), true)

        then: "Record is now retried / dequeued"
        noExceptionThrown()
        response.isDequeued()
    }

}
