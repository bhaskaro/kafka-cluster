package com.oggu.jms.kafkaclient.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 *
 * Author : bhask
 * Created : 04-27-2026
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "${app.kafka.topics.orders}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumeOrders(ConsumerRecord<String, String> record) {
        logRecord("ORDERS", record);
    }

    //same message will be delevered to both the orders group1 and 2
    @KafkaListener(topics = "${app.kafka.topics.orders}", groupId = "scheduled-consumer-group2", concurrency = "2")
    public void consumeOrders2(ConsumerRecord<String, String> record) {
        logRecord("ORDERS", record);
    }

    @KafkaListener(topics = "${app.kafka.topics.payments}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumePayments(ConsumerRecord<String, String> record) {
        logRecord("PAYMENTS", record);
    }

    @KafkaListener(topics = "${app.kafka.topics.shipments}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumeShipments(ConsumerRecord<String, String> record) {
        logRecord("SHIPMENTS", record);
    }

    @KafkaListener(topics = "${app.kafka.topics.notifications}", groupId = "scheduled-consumer-group", concurrency = "2")
    public void consumeNotifications(ConsumerRecord<String, String> record) {
        logRecord("NOTIFICATIONS", record);
    }

    private void logRecord(String type, ConsumerRecord<String, String> record) {
        logger.info("{} | Thread={} | Topic={} | Partition={} | Offset={} | Key={} | Timestamp={} | Message={}",
                type,
                Thread.currentThread().getName(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.timestamp(),
                record.value());
    }
}
