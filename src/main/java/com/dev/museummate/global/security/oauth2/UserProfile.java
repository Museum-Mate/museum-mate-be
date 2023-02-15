package com.dev.museummate.global.security.oauth2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfile {
    private final String email;
    private final String name;

    public UserProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
