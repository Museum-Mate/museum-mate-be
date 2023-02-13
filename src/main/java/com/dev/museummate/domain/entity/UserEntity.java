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
public class UserEntity extends BaseTimeEntity {

    /**
     * id : idx email : 유저 이메일 userName : 유저 닉네임 name : 유저 실명 auth : 이메일 인증 상태 false = 인증 안됨 true = 인증 됨
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
    private Boolean auth = false;
    private java.lang.String authNum = "1234";
    private String providerId;
    private String providerType = "general";


    @Builder
    public UserEntity(Long id, String email, String password, String name,
                      String userName, String birth, String phoneNumber, String address,
                      UserRole role, Boolean auth, String authNum, String providerId, String providerType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userName = userName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.auth = auth;
        this.authNum = authNum;
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

    public void updateAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public void updateAuth() {
        this.auth = true;
    }
}
