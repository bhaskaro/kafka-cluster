package com.oggu.jms.kafkaclient.schduler;

import com.oggu.jms.kafkaclient.config.KafkaConfigProps;
import com.oggu.jms.kafkaclient.producer.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 *
 * Author : bhask
 * Created : 01-02-2026
 */
@Service
public class ScheduledKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledKafkaProducer.class);

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private KafkaConfigProps config;

    @Scheduled(fixedRateString = "${app.scheduler.fixed-rate-ms:5000}")
    public void produceMessages() {
        config.getTopics().values().forEach(topic -> {
            String key = UUID.randomUUID().toString();
            String message = topic + " - " + " test message - " + new Date();
            logger.info("Sending data to topic {}", topic);
            producerService.sendMessage(topic, key, message);
        });
    }
}


