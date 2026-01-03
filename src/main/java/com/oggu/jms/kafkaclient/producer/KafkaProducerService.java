package com.oggu.jms.kafkaclient.producer;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Author : bhask
 * Created : 01-02-2026
 */
@Service
public class KafkaProducerService {

    private static final List<String> TOPICS = List.of("orders", "payments", "shipments", "notifications");
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishMessages() {
        for (String topic : TOPICS) {
            executor.submit(() -> {
                for (int i = 0; i < 25; i++) {
                    String key = UUID.randomUUID().toString();
                    String value = "Message " + i + " from " + topic;
                    kafkaTemplate.send(topic, key, value);
                }
            });
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}

