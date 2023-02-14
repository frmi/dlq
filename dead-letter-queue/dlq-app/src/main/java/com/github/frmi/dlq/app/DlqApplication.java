package com.github.frmi.dlq.app;

import com.github.frmi.dlq.data.DlqRecord;
import com.github.frmi.dlq.data.DlqRecordRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackageClasses = DlqRecord.class)
@EnableJpaRepositories(basePackageClasses = {DlqRecordRepository.class})
@ComponentScan(basePackages = {
        "com.github.frmi.dlq.api",
        "com.github.frmi.dlq.data",
        "com.github.frmi.dlq.app",
        "com.github.frmi.dlq.batch"})
public class DlqApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlqApplication.class, args);
    }

}
