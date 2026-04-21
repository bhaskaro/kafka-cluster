package com.oggu.jms.kafkaclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 *
 * Author : bhask
 * Created : 04-20-2026
 */
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaConfigProps {

    private Map<String, String> topics;

    public Map<String, String> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, String> topics) {
        this.topics = topics;
    }
}
