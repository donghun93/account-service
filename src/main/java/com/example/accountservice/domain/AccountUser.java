package com.example.accountservice.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountUser {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @Builder
    private AccountUser(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}


