package com.hello.debezium.embed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hello.debezium.embed.model.CDCEvent;
import com.hello.debezium.share.repository.MemberRepository;
import com.hello.debezium.share.service.MemberService;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.debezium.engine.spi.OffsetCommitPolicy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@ActiveProfiles({"oracle-local"})
@SpringBootTest
class OracleLocalTest {
    /*
    docker run -d --rm --name oracle-logminer \
     -p 1521:1521 \
     -e ORACLE_PWD=secret \
     -e DEFAULT_USER=foo \
     -e DEFAULT_PWD=foo \
     -e DEFAULT_LOGMINER_USER=c##logminer \
     -e DEFAULT_LOGMINER_PWD=logminer \
    oracle/database:19.3.0-ee-logminer

     */


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    static CountDownLatch countDownLatch = new CountDownLatch(1000);
    static DebeziumEngine<ChangeEvent<String, String>> engine;

    @BeforeAll
    static void oracleTest() throws IOException {
        // Define the configuration for the Debezium Engine with Oracle connector...
        final Properties props = new Properties();
        props.load(new FileInputStream(new File(String.join(File.separator, "src", "test", "resources", "embed", "oracle.properties"))));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        engine = DebeziumEngine.create(Json.class)
                .using(props)
                .using(OffsetCommitPolicy.always())
                .notifying((records, committer) -> {

                    for (ChangeEvent<String, String> r : records) {
                        System.out.println("Key = '" + r.key() + "' value = '" + r.value() + "'");
                        committer.markProcessed(r);
                    }
                })
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
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }).build();
        executor.execute(engine);

    }

    @Test
    @Rollback(value = false)
    void test() throws InterruptedException, IOException {
        System.out.println("=================Try....");
//        List<Member> members = memberRepository.findAll();
//        Assertions.assertThat(members)
//                .hasSizeGreaterThanOrEqualTo(2);
////            Member modifyMember = members.get(0);
////            modifyMember.setName("Modified");
////            memberRepository.saveAndFlush(modifyMember);
////
////            Member deleteMember = members.get(1);
////            memberRepository.deleteById(deleteMember.getId());
////
//        Member modifyMember = members.get(0);
//        modifyMember.setName("Modified");
//        memberService.modify(modifyMember);
//
//        Member deleteMember = members.get(1);
//        memberService.remove(deleteMember.getId());

        System.out.println("=================Done....");

//        final Properties props = new Properties();
//        props.load(new FileInputStream(new File(String.join(File.separator, "src", "test", "resources", "embed", "oracle.properties"))));
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        engine = DebeziumEngine.create(Json.class)
//                .using(props)
//                .using(OffsetCommitPolicy.always())
//                .notifying(record -> {
//                    log.debug("-----------------------------------");
//                    log.debug("headers : {}", record.headers());
//                    log.debug("dest : {}", record.destination());
//                    log.debug("key : {}", record.key());
//                    log.debug("value : {}", record.value());
//
////                    try {
////                        CDCEvent memberCDCEvent = CDCEvent.fromRecordValue(record.value());
////                        log.debug("event : \n{}", memberCDCEvent.toPrettyJson());
////                    } catch (JsonProcessingException e) {
////                        throw new RuntimeException(e);
////                    }
//                    countDownLatch.countDown();
//                }).build();
//        executor.execute(engine);


        countDownLatch.await(1000, TimeUnit.SECONDS);
        engine.close();
        // Do something else or wait for a signal or an event
        // Engine is stopped when the main code is finished
    }

}