package com.dev.museummate.configuration.redis.service;

import com.dev.museummate.configuration.redis.entity.TokenEntity;
import com.dev.museummate.configuration.redis.repository.TokenRepository;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional(readOnly = true)
    public TokenEntity findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                              .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND, "토큰이 존재하지 않습니다."));
    }

    @Transactional
    public void saveToken(Long userId, String refreshToken, String accessToken) {
        tokenRepository.save(new TokenEntity(String.valueOf(userId), refreshToken, accessToken));
    }

    @Transactional
    public void removeToken(String accessToken) {
        tokenRepository.findByAccessToken(accessToken)
                       .ifPresent(tokenEntity -> tokenRepository.delete(tokenEntity));
    }

}
