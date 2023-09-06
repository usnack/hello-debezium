package com.hello.debezium.share.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {
    @Id
    private Long id;
    private String name;
    private int age;
}
