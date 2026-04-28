package com.oggu.jms.kafkaclient.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 *
 * Author : bhask
 * Created : 01-02-2026
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "${app.kafka.topics.orders}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumeOrders(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        logger.info("ORDERS | Thread={} | Topic={} | Partition={} | Offset={} | Timestamp={} | Message={}",
                Thread.currentThread().getName(), topic, partition, offset, timestamp, message);
    }

    @KafkaListener(topics = "${app.kafka.topics.payments}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumePayments(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        logger.info("PAYMENTS | Thread={} | Topic={} | Partition={} | Offset={} | Timestamp={} | Message={}",
                Thread.currentThread().getName(), topic, partition, offset, timestamp, message);
    }

    @KafkaListener(topics = "${app.kafka.topics.shipments}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumeShipments(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        logger.info("SHIPMENTS | Thread={} | Topic={} | Partition={} | Offset={} | Timestamp={} | Message={}",
                Thread.currentThread().getName(), topic, partition, offset, timestamp, message);
    }

    @KafkaListener(topics = "${app.kafka.topics.notifications}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumeNotifications(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        logger.info("NOTIFICATIONS | Thread={} | Topic={} | Partition={} | Offset={} | Timestamp={} | Message={}",
                Thread.currentThread().getName(), topic, partition, offset, timestamp, message);
    }
}

