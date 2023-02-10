package com.dev.museummate.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ROLE_GUEST("ROLE_GUEST", "손님 권한"),
    ROLE_USER("ROLE_USER", "일반 사용자 권한"),
    ROLE_ADMIN("ROLE_ADMIN", "관리자 권한"),
    ROLE_SOCIAL_USER("ROLE_SOCIAL_USER", "소셜 사용자 권한"),
    ;

    private final String roleCode;
    private final String roleDiscription;
}
