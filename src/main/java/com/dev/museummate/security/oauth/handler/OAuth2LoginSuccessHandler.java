package com.dev.museummate.security.oauth.handler;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.domain.UserRole;
import com.dev.museummate.security.oauth.CustomOAuth2User;
import com.dev.museummate.utils.CookieUtils;
import com.dev.museummate.utils.HeaderUtils;
import com.dev.museummate.utils.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;

    @Value("${cookie.maxage}")
    private int maxAge; // for cookie

    /**
     * AccessToken은 헤더에 담아서 보내고, RefreshToken은 쿠키에 담아서 보낸다.
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        // authentication -> principal -> attributes
        log.info("OAuth2 Login 시도");
        try {
            OAuth2User oAuth2User1 = (OAuth2User) authentication.getPrincipal();


            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            if (oAuth2User.getUserRole() == UserRole.ROLE_SOCIAL_USER) {
                log.info("OAuth2 Login 성공!!!");

                log.info("Provider : [{}]", oAuth2User.getAttribute("providertype").toString());
                log.info("Email : [{}]", oAuth2User.getEmail());
                log.info("Name : [{}]", oAuth2User.getName());

                loginSuccess(response, oAuth2User);

            }
        } catch (Exception e) {
            log.info("OAuth2 Login 실패!!!");
            response.sendRedirect("/login");
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        //TODO: 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기

        log.info("OAuth2AuthenticationSuccessHandler - loginSuccess");

        String accessToken = jwtProvider.createAccessToken(oAuth2User.getEmail());

        // accessToken 헤더에 담아서 전송
        HeaderUtils.setAccessTokenHeader(response, accessToken);

        String refreshToken = jwtProvider.createRefreshToken(oAuth2User.getEmail());

        // refreshToken Redis에 저장 (key: RT:email, value: refreshToken)
        redisDao.setValues("RT:" + oAuth2User.getEmail(), refreshToken, maxAge, TimeUnit.SECONDS);

        // refreshToken 쿠키에 담아서 전송 (HttpOnly, Secure)
        CookieUtils.setCookie(response, "refreshToken", refreshToken, maxAge);

        // 로그인 성공 시 리다이렉트
        response.sendRedirect("/");
    }
}


