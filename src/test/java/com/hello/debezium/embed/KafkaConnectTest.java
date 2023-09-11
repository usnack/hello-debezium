package com.hello.debezium.embed;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Slf4j

class KafkaConnectTest {

    @Test
    void topicList() {
        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);

        consumer.subscribe(List.of("server1.FOO.MEMBER"));

        Map<String, List<PartitionInfo>> topics = consumer.listTopics();
        log.info("topic List : {}", topics.size());
        topics.forEach((key, val) -> log.warn("{} : {}", key, val));

        ConsumerGroupMetadata consumerGroupMetadata = consumer.groupMetadata();
        log.warn("group : {}", consumerGroupMetadata);
        while(true) {
            log.debug("try poll...");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
            log.warn("record count : {}", records.count());
        }
    }
}
