package com.github.frmi.dlq.app;

import com.github.frmi.dlq.api.EnableDeadLetterQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDeadLetterQueue
@SpringBootApplication
public class DlqApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlqApplication.class, args);
    }

}
