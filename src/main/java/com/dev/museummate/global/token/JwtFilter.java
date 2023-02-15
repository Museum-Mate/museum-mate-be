package com.dev.museummate.global.token;

import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.global.redis.RedisDao;
import com.dev.museummate.global.utils.CookieUtils;
import com.dev.museummate.global.utils.JwtUtils;
import com.dev.museummate.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final RedisDao redisDao;
    private final JwtUtils jwtUtils;

    private final String LOGIN = "/api/v1/users/login";
    private final String LOGIN_URI = "/api/v1/users/login";
    private final String GOOGLE_LOGIN_URI = "/oauth2/authorization/code/google";
    private final String NAVER_LOGIN_URI = "/oauth2/authorization/code/naver";
    private final String JOIN_URI = "/api/v1/users/join";
    private final String SOCIAL_JOIN = "/join/social";

    @Value("${access-token-maxage}")
    public int accessTokenMaxAge;
    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;

    /**
     * Access Token 은 Header 에 담아서 보내고, Refresh Token 은 Cookie 에 담아서 보낸다.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("jwtFilter 실행");
        log.info("request.getRequestURI() : {}", request.getRequestURI());
        log.info("request.getRequestURL() : {}", request.getRequestURL());

        // 메인페이지 요청일 때 토큰 검증을 하지 않는다.
        if (request.getRequestURI().equals("/")) {
            log.info("메인페이지 요청입니다.");
            filterChain.doFilter(request, response);
            return; // 필터가 더 이상 진행되지 않도록 리턴
        }

        // Login 요청일 때 토큰 검증을 하지 않는다.
        if (request.getRequestURI().equals(LOGIN_URI)
            || request.getRequestURI().equals(LOGIN)) {
            log.info("로그인 요청입니다.");
            filterChain.doFilter(request, response);
            return; // 필터가 더 이상 진행되지 않도록 리턴
        }

        if (request.getRequestURI().equals(JOIN_URI)
            || request.getRequestURI().equals(SOCIAL_JOIN)) {
            log.info("회원가입 요청입니다.");
            filterChain.doFilter(request, response);
            return; // 필터가 더 이상 진행되지 않도록 리턴
        }

        if (request.getRequestURI().equals(GOOGLE_LOGIN_URI)
            || request.getRequestURI().equals(NAVER_LOGIN_URI)) {

            if (request.getRequestURI().equals(GOOGLE_LOGIN_URI)) {
                log.info("구글 로그인 요청입니다.");
            } else if (request.getRequestURI().equals(NAVER_LOGIN_URI)) {
                log.info("네이버 로그인 요청입니다.");
            }
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return; // 필터가 더 이상 진행되지 않도록 리턴
        }

        Cookie[] cookie = request.getCookies();
        log.info("request.getCookies() : {}", (Object[]) request.getCookies());

        Optional<String> accessTokenAtCookie = CookieUtils.extractAccessToken(request);
        Optional<String> refreshTokenAtCookie = CookieUtils.extractRefreshToken(request);

        // 쿠키에 토큰이 없을 때
//        if (accessTokenAtCookie.isEmpty() && refreshTokenAtCookie.isEmpty()) {
//            log.error("모든 토큰이 없습니다.");
//            log.info("request.getRequestURI() : {}", request.getRequestURI());
//            log.info("[AccessTokenAtCookie] : {}", accessTokenAtCookie);
//            log.info("[RefreshTokenAtCookie] : {}", refreshTokenAtCookie);
//            filterChain.doFilter(request, response);
//            return; // 필터가 더 이상 진행되지 않도록 리턴
//        }

        if (accessTokenAtCookie.isEmpty()) {
            throw new JwtException("AccessToken이 없습니다.");
        }

        String accessToken = accessTokenAtCookie.get();

        if (jwtUtils.isValid(accessToken)) {
            throw new JwtException("잘못된 AccessToken 입니다.");
        }

        if (jwtUtils.isExpired(accessToken)) {
            throw new JwtException("만료된 AccessToken 입니다.");
        }

        String email = jwtUtils.getEmail(accessToken);

        UserEntity userEntity =
            userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        // refresh Token 존재 여부 확인
        if (refreshTokenAtCookie.isEmpty()) {
            log.error("Refresh Token이 없습니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());
            throw new JwtException("Refresh Token이 없습니다.");
        }

        String refreshToken = refreshTokenAtCookie.get();
        log.info("refreshToken : {}", refreshToken);

        // access Token 만료된 경우 -> refresh Token 검증
        if (jwtUtils.isExpired(refreshToken)) {
            // refresh Token 만료된 경우
            log.error("Refresh Token 만료");
            log.info("refreshToken : {}", refreshToken);
            throw new JwtException("만료된 RefreshToken 입니다. 다시 로그인 해주세요.");
        }

        // refresh Token 유효한 경우 -> access Token / refresh Token 재발급
        String newAccessToken = jwtUtils.createAccessToken(email);
        log.info("newAccessToken : {}", newAccessToken);
        String newRefreshToken = jwtUtils.createRefreshToken(email);
        log.info("newRefreshToken : {}", newRefreshToken);

        // Redis에 재발급된 refresh Token 저장
//        redisDao.setValues("RT:" + email, newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        userEntity =
            userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        // 발급된 accessToken을 response cookie 에 저장
        CookieUtils.addAccessTokenAtCookie(response, newAccessToken);
        // 발급된 refreshToken을 response cookie 에 저장
        CookieUtils.addRefreshTokenAtCookie(response, newRefreshToken);

        // 유효성 검증 통과한 경우
        log.info("유효성 검증 통과! \n SecurityContextHolder 에 Authentication 객체를 저장합니다!");
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userEntity.getEmail(),
                                                    null,
                                                    List.of(new SimpleGrantedAuthority(userEntity.getRole().name())));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}