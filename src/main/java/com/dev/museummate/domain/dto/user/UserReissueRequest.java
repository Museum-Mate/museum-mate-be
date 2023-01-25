package com.dev.museummate.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserReissueRequest {
    private String accessToken;
    private String refreshToken;
}
