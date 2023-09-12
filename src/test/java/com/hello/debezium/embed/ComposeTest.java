package com.hello.debezium.embed;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Flow;
import java.util.regex.Pattern;

@Slf4j
class ComposeTest {
    static DockerComposeContainer composeContainer = new DockerComposeContainer(Paths.get("debezium", "docker-compose.yaml").toFile())
            .waitingFor("oracle", Wait.forHealthcheck()
                    .withStartupTimeout(Duration.ofMinutes(5)))
            ;

    @BeforeAll
    static void composeUp() throws IOException, InterruptedException {
        log.warn("compose container start...");
        composeContainer.start();

        log.warn("compose container is ready...");
        HttpRequest registerConnectorRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8083/connectors/"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                        "  \"name\": \"tutorial-connector\",\n" +
                        "  \"config\": {\n" +
                        "    \"connector.class\" : \"io.debezium.connector.oracle.OracleConnector\",\n" +
                        "    \"tasks.max\" : \"1\",\n" +
                        "    \"topic.prefix\" : \"server1\",\n" +
                        "    \"database.hostname\" : \"host.docker.internal\",\n" +
                        "    \"database.port\" : \"1521\",\n" +
                        "    \"database.user\" : \"c##logminer\",\n" +
                        "    \"database.password\" : \"logminer\",\n" +
                        "    \"database.dbname\" : \"ORCLCDB\",\n" +
                        "    \"database.pdb.name\" : \"ORCLPDB1\",\n" +
                        "    \"schema.history.internal.kafka.bootstrap.servers\" : \"kafka:9092\",\n" +
                        "    \"schema.history.internal.kafka.topic\": \"schema-changes.inventory\"\n" +
                        "  }\n" +
                        "}"))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> registerConnectorResponse = client.send(registerConnectorRequest, HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(registerConnectorResponse.statusCode())
                .isBetween(200, 299);
    }

    @AfterAll
    static void composeDown() {
        log.warn("compose down...");
        composeContainer.stop();
    }

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
