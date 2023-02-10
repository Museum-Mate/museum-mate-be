package com.dev.museummate.configuration.redis.repository;

import com.dev.museummate.configuration.redis.entity.TokenEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String> {

    Optional<TokenEntity> findByAccessToken(String accessToken);

}
