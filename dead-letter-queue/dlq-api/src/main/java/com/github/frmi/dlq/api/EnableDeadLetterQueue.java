package com.github.frmi.dlq.api;

import com.github.frmi.dlq.api.data.DlqRecord;
import com.github.frmi.dlq.api.data.DlqRecordRepository;
import com.github.frmi.dlq.api.service.DlqService;
import com.github.frmi.dlq.api.web.ApiConfig;
import com.github.frmi.dlq.api.web.DlqRetryController;
import com.github.frmi.dlq.api.web.DlqViewController;
import com.github.frmi.dlq.api.web.error.DeadLetterQueueAdvice;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EntityScan(basePackageClasses = DlqRecord.class)
@EnableJpaRepositories(basePackageClasses = DlqRecordRepository.class)
@Import({DlqViewController.class, DlqRetryController.class, DeadLetterQueueAdvice.class, DlqService.class, ApiConfig.class})
public @interface EnableDeadLetterQueue {
}
