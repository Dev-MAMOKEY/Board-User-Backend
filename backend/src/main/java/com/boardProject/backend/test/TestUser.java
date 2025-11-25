package com.boardProject.backend.test;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TestUser {
    @Id @GeneratedValue
    private Long id;

    private String name;
}
