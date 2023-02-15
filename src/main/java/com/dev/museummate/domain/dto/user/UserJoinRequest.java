package com.dev.museummate.domain.dto.user;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequest {
    private String email;
    private String password;
    private String name;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;

    public UserEntity toEntity(String encodedPassword) {
        return UserEntity.builder()
                .address(this.address)
                .birth(this.birth)
                .email(this.email)
                .name(this.name)
                .userName(this.userName)
                .phoneNumber(this.phoneNumber)
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .auth(false)
                .build();
    }
}
