package com.dev.museummate.security;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.service.UserService;
import com.dev.museummate.utils.CookieUtils;
import com.dev.museummate.utils.HeaderUtils;
import com.dev.museummate.utils.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final RedisDao redisDao;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    private final String LOGIN_URL = "/api/v1/login";
    private final String GOOGLE_LOGIN_URL = "/login/oauth2/code/google";
    private final String SOCIAL_LOGIN_URL = "/login/oauth2/google";

    /**
     * Access Token 은 Header 에 담아서 보내고, Refresh Token 은 Cookie 에 담아서 보낸다.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("jwtFilter 실행");
        log.info("request.getRequestURI() : {}", request.getRequestURI());
        log.info("request.getRequestURL() : {}", request.getRequestURL());

        // Login 요청일 때 토큰 검증을 하지 않는다.
        if (request.getRequestURI().equals(LOGIN_URL)) {
            log.info("로그인 요청입니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().equals(GOOGLE_LOGIN_URL)) {
            log.info("Google 로그인 요청입니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().equals(SOCIAL_LOGIN_URL)) {
            log.info("소셜 로그인 요청입니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> token = HeaderUtils.extractAccessToken(request);
        Optional<Cookie> cookie = CookieUtils.getCookie(request, "refreshToken");

        if (token.isEmpty() && cookie.isEmpty()) {
            log.info("토큰이 없습니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = token.get();
        String refreshToken = cookie.get().getValue();

        // 유효성 검증
        if (jwtProvider.isValid(accessToken) || jwtProvider.isValid(refreshToken)) {
            log.info("유효하지 않은 토큰입니다.");
            filterChain.doFilter(request, response);
            return;
        }

        String newAccessToken = null;
        // 만료 검증
        if (jwtProvider.isExpired(accessToken)) {
            log.info("[accessToken]이 만료되었습니다.");
            log.info("[refreshToken]이 만료되었는지 확인합니다.");
            if (jwtProvider.isExpired(refreshToken)) {
                log.info("[refreshToken] 이 만료되었습니다.");
                log.info("로그인 페이지로 리다이렉트 합니다.");
                filterChain.doFilter(request, response);
                return;
            } else {
                log.info("[refreshToken]이 유효합니다.");
                log.info("[accessToken]을 재발급합니다.");
                newAccessToken = jwtProvider.createAccessToken(refreshToken);
                log.info("새로운 [accessToken] : [{}]", newAccessToken);
            }
        }

        String email = jwtProvider.getEmail(newAccessToken);
        UserEntity userEntity = userService.findUserByEmail(email);
        String userRole = userEntity.getRole().name();

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority(userRole)));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request,response);
    }
}
