# Dead Letter Queue
The idea with this project is to create an API to ease the setup of a Dead Letter Queue (dlq).

The motivation for creating this project is the need for a Dead Letter Queue in a Microservice landscape using Kafka.
However, the dlq project is made such that the user defines the structure of each message pushed to dlq as long as it 
can be formatted as a string. The user must then implement its own retry mechanism. The library will simply call an 
interface when a message is requested retried, see [Usage](#Usage).

## Structure
The repository consists of two projects:
1. dlq
   1. dlq-api  
   The API of dlq.
   2. dlq-app  
   This is a showcase of using the dlq api to implement a Dead Letter Queue for Kafka
2. sample kafka app  
This is a sample app for consuming Kafka messages and pushing erroneous messages to dql.

### DLQ
The API consists of a web, service, and data layer.
#### Web  
A REST API with the following spec. You can optionally add OpenAPI to your implementation.

##### API Summary

**POST /push**  
Push a record to dql.

**GET /retry/{id}**  
Retry a given record.

_Query parameters_  
* forceRetry (true/false) \[optional\]  
Records can by default only be retried once if successful the first time.   
  Use this flag to override this behavior. 


**GET /find/{id}**  
Retrieve one specific record.

**GET /all**  
Retrieve all records waiting for retry.  

_Query parameters_
* includeRetried (true**/false) \[optional\]  
By default records that has successfully been retried are not included in the 
    response of this request.  
    Use this flag to overwrite this behaviour.

#### Service
Defines an abstraction between the Web and Data layer and defining the interface to be used by the implementer:
```java
/**
 * Defines how the Dead Letter Queue records should be retried.
 */
public interface DlqRetry {

    /**
     * Retry mechanism.
     * @param record The record requested retried.
     * @return {@code true} if retried successfully; {@code false} otherwise.
     */
    boolean retry(DlqRecord record);

}
```

While it is required to implement the `DlqRetry` it is also required to add the `@EnableDeadLetterQueue` annotation to 
your application or a configuration bean within your application. This annotation imports the necessary beans from the API.

#### Data
DLQ stores data in one table: _DLQ_RECORD_
```java
@Entity
public class DlqRecord {

    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dlq_record_sequence_generator")
    @SequenceGenerator(name = "dlq_record_sequence_generator", sequenceName = "dlq_record_sequence")
    private Long id;

    /**
     * Record created at.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Depicts whether the record has successfully been retried.
     */
    private boolean dequeued;

    /**
     * Content of the message that holds everything for the user to retry this record.
     */
    @Lob
    @Column(nullable = false)
    private String message;

    /**
     * The exception that caused this record to be sent to dlq.
     */
    @Lob
    private String exception;
    
    // CTOR and Getters and setters...
}
```

## Usage

1. Setup your datasource. Your implementation must define the datasource properties and create the database schema.  
The API has an initial schema script. Thq dlq-app script showcases how to initialize the database using liquibase.
2. Define a pojo that can contain the erroneous records and other information you need to retry this later and can be 
serialized to a string
3. Create implementation of [DlqRetry](https://github.com/frmi/dlq/blob/main/dead-letter-queue/dlq-api/src/main/java/com/github/frmi/dlq/api/service/DlqRetry.java)
4. (Optional) Add OpenAPI for documentation and UI for the REST endpoints.
5. Push record
6. Retry record

### Kafka sample
Sample implementation of retry for a Kafka broker
#### 1. Setup datasource and database generation
_pom.xml_ - Add Liquibase dependency
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
    <version>4.11.0</version>
</dependency>
```
_application.properties:_
```properties
#postgres
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.generate-ddl=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# database initialization 
# Set this to false if you do not want the application to create its own schema
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog-master.xml
```

#### 2. Define pojo
_DlqEntry.java_
```java
public class DlqEntry {
    private String topic;
    private int partition;
    private long timestamp;
    private String value;
    private String key;
    private long offset;

    // Getters and Setters
}
```

#### 3. Implement DlqRetry
_DlqConfig.java_
```java
@EnableDeadLetterQueue
@Configuration
public class DlqConfig {

    @Bean
    public DlqRetry retryer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new KafkaRetry(kafkaTemplate, objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
```

_KafkaRetry.java_
```java
public class KafkaRetry implements DlqRetry {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRetry.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public KafkaRetry(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean retry(DlqRecord record) {

        try {
            DlqEntry entry = objectMapper.readValue(record.getMessage(), DlqEntry.class);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(entry.getTopic(), entry.getPartition(), entry.getKey(), entry.getValue());
            kafkaTemplate.send(producerRecord);
            LOGGER.info("Record has been retried. " + record);
            return true;
        } catch (Exception e) {
            LOGGER.error("Record could not be retried. " + record, e);
        }

        return false;
    }
}
```

#### 4. (Optional) Add OpenAPI for documentation and UI for the REST endpoints.

_pom.xml_
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.9</version>
</dependency>
```
http://localhost:8080/dlq/swagger-ui/index.html

#### 5. Push record
Push records using a `RestTemplate`:
```java

public void handleRecord(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
    RestTemplate restTemplate = new RestTemplate();
    DlqRecordDto recordDto = new DlqRecordDto();

    StringWriter writer = new StringWriter();
    PrintWriter printer = new PrintWriter(writer);
    thrownException.printStackTrace(printer);
    recordDto.setException(writer.toString());

    try {
        ObjectMapper mapper = new ObjectMapper();
    
        DlqEntry entry = new DlqEntry();
        entry.setKey((String)record.key());
        entry.setOffset(record.offset());
        entry.setPartition(record.partition());
        entry.setTimestamp(record.timestamp());
        entry.setTopic(record.topic());
        entry.setValue(mapper.writeValueAsString(record.value()));
        recordDto.setMessage(mapper.writeValueAsString(entry));
        ResponseEntity<DlqRecordResponseDto> response = restTemplate.postForEntity("http://localhost:8080/dlq/push", recordDto, DlqRecordResponseDto.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            LOGGER.error("Error thrown during handling of record " + record + ". Persisted as " + response, thrownException);
        } else {
            LOGGER.error("Error pushing record " + record + ". Response " + response);
        }

    } catch (JsonProcessingException e) {
        LOGGER.error("Error serializing value of record " + record, e);
    }
}
```

#### 6. Retry record
_Curl request_
```curl
curl -X 'GET' \
  'http://localhost:8080/dlq/retry/10' \
  -H 'accept: application/json'
```

## Run the sample
1. Install 
```
cd dead-letter-queue
mvn clean install
```
2. Make sure you have Kafka running.  
If you do not you can get Kafka and a client using docker:
```
docker run -p 80:80 -p 9092:9092 -p 8083:8083 -p 8081:8081 \
 -e FULLENV=true \
 -e DEMO_ARGS="true true" \
 -e ADV_HOST=127.0.0.1:9092 \
 xeotek/kadeck-allinone:latest
```
This will create containers for Kafka, zookeeper and Kadeck Web (username=admin, password=admin). See https://support.xeotek.com/hc/en-us/articles/4611836680722-Run-KaDeck-Web-All-In-One-Box-to-test-your-applications

3. Make sure you have postgresql running.  
The project has a docker-compose file to setup a postgresql and a pgadmin container.
```
cd dead-letter-queue/dlq-app/docker
docker-compose -f postgres.yml up
```
4. Start `dlq-app`
```
mvn spring-boot:run -f pom.xml
```
5. Start `sample-kafka-app`
```
cd sample-kafka-app
mvn clean install
mvn spring-boot:run -f pom.xml
```
6. Go to Kadeck (you started this in step 2) http://localhost/#/topicbrowser/1/sample.topic/
7. Produce a message  
the `sample-kafka-app` is throwing an exception if the id of the message equals `2`. 
Otherwise the message is processed correct.
```json
{ "id": 2, "greeting": "hello" }
```
8. Check that the message was successfully pushed to dead letter queue
```
curl -X 'GET' \
  'http://localhost:8080/dlq/all' \
  -H 'accept: application/json'
```
9. Retry the message note the id and insert it in below curl command.
```
curl -X 'GET' \
  'http://localhost:8080/dlq/retry/<ID>' \
  -H 'accept: application/json'
```
