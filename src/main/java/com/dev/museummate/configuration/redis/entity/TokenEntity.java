package com.dev.museummate.configuration.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@Getter
@RedisHash(value = "Token", timeToLive = 60 * 60 * 24 * 3) // 3일
public class TokenEntity {

    @Id
    private String id;
    private String refreshToken;
    @Indexed
    private String accessToken;

}
