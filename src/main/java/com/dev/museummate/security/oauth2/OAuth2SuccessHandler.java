package com.dev.museummate.security.oauth2;

import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.utils.CookieUtils;
import com.dev.museummate.utils.HeaderUtils;
import com.dev.museummate.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

            response.sendRedirect(getRedirectURI(accessToken, refreshToken, checkUser.get().getId()));
//            response.sendRedirect("/login?"
//                                      + "accessToken=" + accessToken
//                                      + "&refreshToken=" + refreshToken
//                                      + "&userId=" + checkUser.get().getId());
        } else {
            log.info("가입되지 않은 유저입니다.");

            // 쿠키에 유저정보 저장 후 회원가입 페이지에서 꺼내서 활용 (이메일, 이름)
            String newEmail = oAuth2User.getAttribute("email");
            String newName = oAuth2User.getAttribute("name");
            String accessToken = jwtUtils.createAccessToken(newEmail, newName);
            String refreshToken = jwtUtils.createRefreshToken(newEmail, newName);

            // header 추가
            HeaderUtils.addAccessTokenHeader(response, accessToken);
            CookieUtils.addRefreshTokenAtCookie(response, refreshToken);
            log.info("추가정보 기입을 위해서 회원가입 페이지로 리다이렉트 합니다.");
            // 추가정보 기입을 위해서 회원가입 페이지로 리다이렉트
            response.sendRedirect("/join/social");
        }
    }

    private String getRedirectURI(String accessToken, String refreshToken, Long userId) {
        return UriComponentsBuilder.fromUriString("http://localhost:8080/login")
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken)
            .queryParam("userId", userId)
            .build()
            .toUriString();
    }
}