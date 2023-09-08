package com.hello.debezium.share.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {
    @Id
    private String id;
    private String name;
    private int age;

    public Member(String name, int age) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
    }
}
