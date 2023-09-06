package com.hello.debezium.embed.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Optional;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CDCEvent {
    private Payload payload;
    private String op;

    @Getter
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private Object before;
        private Object after;

        private String db;
        private String schema;
        private String table;
    }

    public static CDCEvent fromRecordValue(String record) throws JsonProcessingException {
        CDCEvent.CDCEventBuilder eventBuilder = CDCEvent.builder();
        Payload.PayloadBuilder payloadBuilder = Payload.builder();

        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap payload = Optional.ofNullable(objectMapper.readValue(record, LinkedHashMap.class).get("payload")).map(LinkedHashMap.class::cast).orElse(new LinkedHashMap());
        payloadBuilder
                .before(payload.get("before"))
                .after(payload.get("after"));

        LinkedHashMap source = Optional.ofNullable(payload.get("source")).map(LinkedHashMap.class::cast).orElse(new LinkedHashMap<>());
        String db = Optional.ofNullable(source.get("db")).map(String.class::cast).orElse(null);
        String schema = Optional.ofNullable(source.get("schema")).map(String.class::cast).orElse(null);
        String table = Optional.ofNullable(source.get("table")).map(String.class::cast).orElse(null);
        payloadBuilder
                .db(db)
                .schema(schema)
                .table(table);

        eventBuilder
                .payload(payloadBuilder.build());

        String op = Optional.ofNullable(payload.get("op")).map(String.class::cast).orElse(null);
        eventBuilder
                .op(op);

        return eventBuilder.build();
    }

    public String toPrettyJson() throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
