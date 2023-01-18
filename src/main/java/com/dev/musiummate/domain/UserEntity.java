package com.dev.musiummate.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;

    @Builder
    public UserEntity(Long id, String email, String password, String userName, String birth, String phoneNumber, String address) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
