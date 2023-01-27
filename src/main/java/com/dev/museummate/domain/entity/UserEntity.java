package com.dev.museummate.domain.entity;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.dto.user.UserModifyRequest;
import jakarta.persistence.*;
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

public class UserEntity extends BaseEntity{

    /**
     * id : idx
     * email : 유저 이메일
     * userName : 유저 닉네임
     * name : 유저 실명
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private String userName;
    private String birth;
    private String phoneNumber;
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
