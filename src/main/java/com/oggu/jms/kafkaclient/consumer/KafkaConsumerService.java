package com.oggu.jms.kafkaclient.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 *
 * Author : bhask
 * Created : 01-02-2026
 */
@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = {"orders", "payments", "shipments", "notifications"},
            concurrency = "4",
            groupId = "scheduled-consumer-group"
    )
    public void consume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.out.printf("Thread=%s | Topic=%s | Partition=%d | Message=%s%n",
                Thread.currentThread().getName(),
                topic, partition, message
        );
    }
}

