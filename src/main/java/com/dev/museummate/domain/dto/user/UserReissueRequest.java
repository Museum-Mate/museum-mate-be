package com.dev.museummate.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReissueRequest {
    private String accessToken;
    private String refreshToken;
}
