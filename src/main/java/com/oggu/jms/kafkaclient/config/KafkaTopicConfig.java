package com.oggu.jms.kafkaclient.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 *
 * Author : bhask
 * Created : 04-20-2026
 */
@Configuration
public class KafkaTopicConfig {

    private static @NonNull NewTopic createTopic(String notifications) {
        return TopicBuilder.name(notifications)
                .partitions(4)
                .replicas(3)
                .config("retention.ms", "60000") // 60 seconds - not recommended for prod. set it to 7 days.
                .build();
    }

    @Bean
    public NewTopic ordersTopic() {
//        return new NewTopic("orders", 4, (short) 3);
        return createTopic("orders");
    }

    @Bean
    public NewTopic paymentsTopic() {
//        return new NewTopic("payments", 4, (short) 3);
        return createTopic("payments");
    }

    @Bean
    public NewTopic shipmentsTopic() {
//        return new NewTopic("shipments", 4, (short) 3);
        return createTopic("shipments");
    }

    @Bean
    public NewTopic notificationsTopic() {
//        return new NewTopic("notifications", 4, (short) 3);
        return createTopic("notifications");
    }

}
