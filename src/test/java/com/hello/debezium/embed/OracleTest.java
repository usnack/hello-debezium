package com.hello.debezium.embed;

import com.hello.debezium.share.entity.Member;
import com.hello.debezium.share.repository.MemberRepository;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("oracle")
class OracleTest {
    //
    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    void oracleTest() throws IOException {
        // Define the configuration for the Debezium Engine with Oracle connector...
        final Properties props = new Properties();
        props.load(new FileInputStream(new File(String.join(File.separator, "src", "test", "resources", "embed", "oracle.properties"))));

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

            System.out.println("=================Try....");
            List<Member> members = memberRepository.findAll();
            Assertions.assertThat(members)
                            .hasSizeGreaterThanOrEqualTo(2);
            Member modifyMember = members.get(0);
            modifyMember.setName("Modified");
            memberRepository.saveAndFlush(modifyMember);

            Member deleteMember = members.get(1);
            memberRepository.deleteById(deleteMember.getId());

            System.out.println("=================Done....");

            Thread.sleep(1000*60*10);

            // Do something else or wait for a signal or an event
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Engine is stopped when the main code is finished
    }

}
