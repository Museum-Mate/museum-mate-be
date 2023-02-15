package com.dev.museummate.global.security.oauth2;

import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.global.utils.CookieUtils;
import com.dev.museummate.global.utils.JwtUtils;
import com.dev.museummate.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    public String secretKey;
    @Value("${jwt.access.expiration}")
    public Long accessTokenExpiration;
    @Value("${jwt.refresh.expiration}")
    public Long refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        log.info("OAuth2SuccessHandler.onAuthenticationSuccess");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String socialEmail = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        log.info("email, name : [{}, {}]", socialEmail, name);

        Optional<UserEntity> checkUser =
            userRepository.findByEmailAndName(socialEmail, name);

        if (checkUser.get().getUserName() != null) {
            log.info("이미 가입된 유저입니다.");

            String email = checkUser.get().getEmail();
            String accessToken = jwtUtils.createAccessToken(email);
            String refreshToken = jwtUtils.createRefreshToken(email);
            log.info("accessToken: {}", accessToken);
            log.info("refreshToken: {}", refreshToken);

            // 쿠키에 토큰 저장
            CookieUtils.addAccessTokenAtCookie(response, accessToken);
            CookieUtils.addRefreshTokenAtCookie(response, refreshToken);

            response.sendRedirect(getDefaultTargetUrl());

        } else {
            log.info("가입되지 않은 유저입니다.");

            // 쿠키에 유저정보 저장 후 회원가입 페이지에서 추가정보 입력 시 꺼내서 활용 (이메일, 이름)
            String newEmail = oAuth2User.getAttribute("email");
            String newName = oAuth2User.getAttribute("name");
            String accessToken = jwtUtils.createAccessToken(newEmail, newName);
            String refreshToken = jwtUtils.createRefreshToken(newEmail, newName);
            log.info("accessToken: {}", accessToken);
            log.info("refreshToken: {}", refreshToken);

            // 쿠키에 토큰 저장
            CookieUtils.addAccessTokenAtCookie(response, accessToken);
            CookieUtils.addRefreshTokenAtCookie(response, refreshToken);

            // 추가정보 기입을 위해서 회원가입 페이지로 리다이렉트
            log.info("추가정보 기입을 위해서 회원가입 페이지로 리다이렉트 합니다.");
            response.sendRedirect("/join/social");
        }
    }

    private String getRedirectURI(String accessToken, String refreshToken, Long userId) {
        String targetUrl = getDefaultTargetUrl();
        return UriComponentsBuilder.fromUriString("http://localhost:8080/login")
                                   .queryParam("accessToken", accessToken)
                                   .queryParam("refreshToken", refreshToken)
                                   .queryParam("userId", userId)
                                   .build()
                                   .toUriString();
    }
}
