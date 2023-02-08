package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.user.UserModifyRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET deleted_at = current_timestamp where id = ?")
@Where(clause = "deleted_at is NULL")
public class UserEntity extends BaseTimeEntity{

    /**
     * id : idx
     * email : 유저 이메일
     * userName : 유저 닉네임
     * name : 유저 실명
     * auth : 이메일 인증 상태 false = 인증 안됨 true = 인증 됨
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;
    @Column(unique = true)
    @NotNull
    private java.lang.String email;
    @NotNull
    private java.lang.String password;
    @NotNull
    private java.lang.String name;
    @NotNull
    private java.lang.String userName;
    @NotNull
    private java.lang.String birth;
    @NotNull
    private java.lang.String phoneNumber;
    @NotNull
    private java.lang.String address;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Boolean auth;
    private java.lang.String authNum;
    private java.lang.String providerId;
    private String providerType;


    @Builder
    public UserEntity(Long id, java.lang.String email, java.lang.String password, java.lang.String name, java.lang.String userName, java.lang.String birth, java.lang.String phoneNumber,
                      java.lang.String address,
                      UserRole role, java.lang.String providerId, String providerType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.auth = false;
        this.authNum = "1234";
        this.providerId = providerId;
        this.providerType = providerType;
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
    public void updateAuthNum(java.lang.String authNum) {
        this.authNum = authNum;
    }

    public void updateAuth() {
        this.auth = true;
    }
}
