package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.user.UserModifyRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
     * userName : 유저 닉네임
     * name : 유저 실명
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;
    @Column(unique = true)
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String userName;
    @NotNull
    private String birth;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String address;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public UserEntity(Long id, String email, String password, String name, String userName, String birth, String phoneNumber, String address, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public void updateInfo(UserModifyRequest userModifyRequest) {
        if (userModifyRequest.getUserName().length() > 0) {
            this.userName = userModifyRequest.getUserName();
        }
        if (userModifyRequest.getPhoneNumber().length() > 0) {
            this.phoneNumber = userModifyRequest.getPhoneNumber();
        }
        if (userModifyRequest.getAddress().length() > 0) {
            this.address = userModifyRequest.getAddress();
        }
    }
}
