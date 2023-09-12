package com.hello.debezium.embed;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j

class KafkaConnectTest {

    @Test
    void topicList() {
        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);

        consumer.subscribe(Pattern.compile("server1.*"));

        ConsumerGroupMetadata consumerGroupMetadata = consumer.groupMetadata();
        log.warn("group : {}", consumerGroupMetadata);
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
            log.warn("record count : {}", records.count());
            for (ConsumerRecord<String, String> record : records) {
                log.warn("topic: {}\nkey: {}\nvalue: {}", record.topic(), record.key(), record.value());
            }
        }
    }
}
