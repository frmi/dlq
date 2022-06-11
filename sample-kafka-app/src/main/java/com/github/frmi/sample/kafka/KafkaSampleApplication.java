package com.github.frmi.sample.kafka;

import com.github.frmi.sample.kafka.model.Greeting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class KafkaSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSampleApplication.class, args);
    }

    @KafkaListener(topics = "${kafka.sample.topic}", groupId = "${kafka.groupId}")
    public void listenGroupFoo(Greeting message) {
        if (message.getId() == 2) {
            throw new RuntimeException("Id cannot be 2!");
        }
        System.out.println("Received Greeting: " + message.getGreeting());
    }
}
