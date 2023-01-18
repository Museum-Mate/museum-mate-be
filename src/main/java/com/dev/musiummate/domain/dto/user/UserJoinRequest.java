package com.dev.musiummate.domain.dto.user;

import com.dev.musiummate.domain.UserRole;
import com.dev.musiummate.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserJoinRequest {
    private String email;
    private String password;
    private String userName;
    private String birth;
    private String phoneNumber;
    private String address;

    public UserEntity toEntity(String encodedPassword) {
        return UserEntity.builder()
                .address(this.address)
                .birth(this.birth)
                .email(this.email)
                .userName(this.userName)
                .phoneNumber(this.phoneNumber)
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();
    }
}
