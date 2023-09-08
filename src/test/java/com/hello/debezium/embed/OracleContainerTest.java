package com.hello.debezium.embed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hello.debezium.embed.model.CDCEvent;
import com.hello.debezium.share.entity.Member;
import com.hello.debezium.share.repository.MemberRepository;
import com.hello.debezium.share.service.MemberService;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles({"oracle"})
@SpringBootTest
@Testcontainers
class OracleContainerTest {
    static class Constants {
        static final String ORACLE_PWD = "secret";
        static final String DEFAULT_USER = "foo";
        static final String DEFAULT_PWD = "foo";
        static final String DEFAULT_LOGMINER_USER = "c##logminer";
        static final String DEFAULT_LOGMINER_PWD = "logminer";
    }

    @Container
    static OracleContainer oracleContainer = new OracleContainer(DockerImageName.parse("oracle/database:19.3.0-ee-logminer")
            .asCompatibleSubstituteFor("gvenzl/oracle-xe"))
            .withExposedPorts(1521)
            .withEnv("ORACLE_PWD", Constants.ORACLE_PWD)
            .withEnv("DEFAULT_USER", Constants.DEFAULT_USER)
            .withEnv("DEFAULT_PWD", Constants.DEFAULT_PWD)
            .withEnv("DEFAULT_LOGMINER_USER", Constants.DEFAULT_LOGMINER_USER)
            .withEnv("DEFAULT_LOGMINER_PWD", Constants.DEFAULT_LOGMINER_PWD)
            .withFileSystemBind(Paths.get("src", "test", "resources", "embed", "startup", "sample.sh").toAbsolutePath().toString(), "/opt/oracle/scripts/startup/Z.201.sample.sh");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> String.format("jdbc:oracle:thin:@localhost:%s/ORCLPDB1", oracleContainer.getMappedPort(1521)));
        registry.add("spring.datasource.username", () -> Constants.DEFAULT_USER);
        registry.add("spring.datasource.password", () -> Constants.DEFAULT_PWD);
    }
    //
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    static CountDownLatch countDownLatch = new CountDownLatch(10);
    static DebeziumEngine<ChangeEvent<String, String>> engine;

    @BeforeAll
    static void oracleTest() throws IOException {
        // Define the configuration for the Debezium Engine with Oracle connector...
        final Properties props = new Properties();
        props.load(new FileInputStream(new File(String.join(File.separator, "src", "test", "resources", "embed", "oracle.properties"))));
        props.setProperty("database.port", oracleContainer.getMappedPort(1521).toString());
        props.setProperty("database.user", Constants.DEFAULT_LOGMINER_USER);
        props.setProperty("database.password", Constants.DEFAULT_LOGMINER_PWD);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        engine = DebeziumEngine.create(Json.class)
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

    }

    @Test
    @Rollback(value = false)
    void test() throws InterruptedException, IOException {
        System.out.println("=================Try....");
        List<Member> members = memberRepository.findAll();
        Assertions.assertThat(members)
                .hasSizeGreaterThanOrEqualTo(2);
//            Member modifyMember = members.get(0);
//            modifyMember.setName("Modified");
//            memberRepository.saveAndFlush(modifyMember);
//
//            Member deleteMember = members.get(1);
//            memberRepository.deleteById(deleteMember.getId());
//
        Member modifyMember = members.get(0);
        modifyMember.setName("Modified");
        memberService.modify(modifyMember);

        Member deleteMember = members.get(1);
        memberService.remove(deleteMember.getId());

        System.out.println("=================Done....");


        countDownLatch.await(1000, TimeUnit.SECONDS);
        engine.close();
        // Do something else or wait for a signal or an event
        // Engine is stopped when the main code is finished
    }

}