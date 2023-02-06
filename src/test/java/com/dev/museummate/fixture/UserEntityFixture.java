package com.dev.museummate.fixture;

import com.dev.museummate.domain.UserRole;
import com.dev.museummate.domain.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity getUser(String email, String password) {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email(email)
                .userName("userName")
                .password(password)
                .role(UserRole.ROLE_USER)
                .build();

        return user;
    }

    public static UserEntity getAdmin(String email, String password) {
        UserEntity admin = UserEntity.builder()
                .id(1L)
                .email(email)
                .password(password)
                .role(UserRole.ROLE_ADMIN)
                .build();

        return admin;
    }
}
