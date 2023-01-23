package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity{

    /**
     * id : idx
     * email : 유저 이메일
     * userName : 유저 실명
     */
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public UserEntity(Long id, String email, String password, String userName, String birth, String phoneNumber, String address, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }
}
