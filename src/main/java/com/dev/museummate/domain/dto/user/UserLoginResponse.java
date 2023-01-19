package com.dev.museummate.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginResponse {
    private String accessToken;
    private String refreshToken;

}
