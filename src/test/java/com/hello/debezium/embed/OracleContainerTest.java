package com.hello.debezium.embed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hello.debezium.embed.model.CDCEvent;
import com.hello.debezium.share.entity.Member;
import com.hello.debezium.share.repository.MemberRepository;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("oracle")
class OracleContainerTest {
    /*
    docker run -it --rm \
        --name oracle \
        -p 1521:1521 \
        -e ORACLE_PWD=top_secret \
        oracle/database:19.3.0-ee-logminer
     */
    @Container
    static OracleContainer oracleContainer = new OracleContainer(DockerImageName.parse("oracle/database:19.3.0-ee-logminer")
            .asCompatibleSubstituteFor("gvenzl/oracle-xe"))

            .withExposedPorts(1521)
            .withEnv("ORACLE_PWD", "top_secret");
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> String.format("jdbc:oracle:thin:@localhost:%s/ORCLPDB1", oracleContainer.getMappedPort(1521)));
    }
    //
    @Autowired
    MemberRepository memberRepository;

    @Test
    void oracleTest() throws IOException {
        // Define the configuration for the Debezium Engine with Oracle connector...
        final Properties props = new Properties();
        props.load(new FileInputStream(new File(String.join(File.separator, "src", "test", "resources", "embed", "oracle.properties"))));
        props.setProperty("database.port", oracleContainer.getMappedPort(1521).toString());
        CountDownLatch countDownLatch = new CountDownLatch(2);
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                    .using(props)
                    .notifying(record -> {
                        log.debug("-----------------------------------");
                        log.debug("headers : {}", record.headers());
                        log.debug("dest : {}", record.destination());
                        log.debug("key : {}", record.key());
                        log.debug("value : {}", record.value());

                        try {
                            CDCEvent memberCDCEvent = CDCEvent.fromRecordValue(record.value());
                            log.debug("event : \n{}", memberCDCEvent.toPrettyJson());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        countDownLatch.countDown();
                    }).build();
            executor.execute(engine);


            System.out.println("=================Try....");
            Member member = new Member(1L, "Foo", 30);
            memberRepository.saveAndFlush(member);
            System.out.println("=================Done....");

            countDownLatch.await(10, TimeUnit.SECONDS);
            engine.close();
            // Do something else or wait for a signal or an event
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Engine is stopped when the main code is finished
    }
}