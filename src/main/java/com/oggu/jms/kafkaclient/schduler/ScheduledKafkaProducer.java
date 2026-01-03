package com.oggu.jms.kafkaclient.schduler;

import com.oggu.jms.kafkaclient.producer.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * Author : bhask
 * Created : 01-02-2026
 */
@Service
public class ScheduledKafkaProducer {

    @Autowired
    private KafkaProducerService producerService;

    @Scheduled(fixedRate = 5000)
    public void produceMessages() {
        producerService.publishMessages();
    }
}


