package com.oggu.jms.kafkaclient.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * Author : bhask
 * Created : 01-02-2026
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // ✅ Generic method
    public void sendMessage(String topic, String key, String message) {

        logger.info("send message to topic {}, key {}, message: {}", topic, key, message);
        kafkaTemplate.send(topic, key, message);
    }

}

