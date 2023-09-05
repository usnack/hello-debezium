package com.hello.debezium.embed;

import com.hello.debezium.share.repository.MemberRepository;
import io.debezium.connector.mysql.MySqlConnector;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("mysql")
class MySQLTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    void mysqlTest() throws IOException {
        // Define the configuration for the Debezium Engine with MySQL connector...
        final Properties props = new Properties();
        props.load(new FileInputStream(new File(String.join(File.separator, "src", "test", "resources", "embed", "mysql.properties"))));

        // Create the engine with this configuration ...
        try (DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(record -> {
                    System.out.println("-----------------------------------");
                    System.out.println("headers : " + record.headers());
                    System.out.println("dest : " + record.destination());
                    System.out.println("key : " + record.key());
                    System.out.println("value : " + record.value());

                }).build()
        ) {
            // Run the engine asynchronously ...
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(engine);

            Thread.sleep(1000*60*10);

            // Do something else or wait for a signal or an event
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Engine is stopped when the main code is finished
    }

}
