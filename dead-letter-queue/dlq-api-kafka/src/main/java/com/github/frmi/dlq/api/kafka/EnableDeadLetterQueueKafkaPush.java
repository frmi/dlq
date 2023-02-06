package com.github.frmi.dlq.api.kafka;

import com.github.frmi.dlq.api.EnableDeadLetterQueue;
import com.github.frmi.dlq.api.web.DlqPushController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DlqKafkaConsumer.class})
@ConditionalOnBean(annotation = EnableDeadLetterQueue.class)
public @interface EnableDeadLetterQueueKafkaPush {
}
