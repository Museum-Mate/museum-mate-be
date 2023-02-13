package com.dev.museummate.global.redis.repository;

import com.dev.museummate.global.redis.entity.TokenEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String> {

    Optional<TokenEntity> findByAccessToken(String accessToken);
    Optional<TokenEntity> findByRefreshToken(String refreshToken);
    Optional<TokenEntity> findByUserId(String userId);
    Optional<TokenEntity> findByAccessTokenAndRefreshToken(String accessToken, String refreshToken);

}
