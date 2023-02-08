package com.dev.museummate.security;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.configuration.redis.entity.TokenEntity;
import com.dev.museummate.configuration.redis.service.TokenService;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.utils.CookieUtils;
import com.dev.museummate.utils.HeaderUtils;
import com.dev.museummate.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;

    private final String LOGIN = "/login";
    private final String LOGIN_URI = "/api/v1/users/login";
    private final String GOOGLE_LOGIN_URI = "/oauth2/authorization/code/google";
    private final String NAVER_LOGIN_URI = "/oauth2/authorization/code/naver";
    private final String JOIN_URI = "/join";

    @Value("${jwt.secret}")
    public String secretKey;
    @Value("${jwt.access.expiration}")
    public Long accessTokenExpiration;
    @Value("${jwt.refresh.expiration}")
    public Long refreshTokenExpiration;

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
        if (request.getRequestURI().equals("/")
            ||request.getRequestURI().equals(LOGIN_URI)
            || request.getRequestURI().equals(GOOGLE_LOGIN_URI)
            || request.getRequestURI().equals(NAVER_LOGIN_URI)
            || request.getRequestURI().equals(LOGIN)
            || request.getRequestURI().equals(JOIN_URI)) {
            log.info("로그인 요청입니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return; // 필터가 더 이상 진행되지 않도록 리턴
        }

        Optional<String> tokenAtHeader = HeaderUtils.extractAccessToken(request);
        Optional<Cookie> tokenAtCookie = CookieUtils.getCookie(request, "Authorization-refreshToken");

        // 헤더에 토큰이 없고, 쿠키에 토큰이 없을 때
        if (tokenAtHeader.isEmpty() && tokenAtCookie.isEmpty()) {
            log.error("모든 토큰이 없습니다.");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("tokenAtHeader : {}", tokenAtHeader);
            log.info("tokenAtCookie : {}", tokenAtCookie);
            filterChain.doFilter(request, response);
            return; // 필터가 더 이상 진행되지 않도록 리턴
        }

        String accessToken = String.valueOf(tokenAtHeader.get());
//        String refreshToken = tokenAtCookie.get().getValue();
        log.info("accessToken : {}", accessToken);
//        log.info("refreshToken : {}", refreshToken);

        UserEntity userEntity;
        try {
            if (jwtUtils.isValid(accessToken)) {
                throw new JwtException ("잘못된 AccessToken 입니다.");
            }

            if (jwtUtils.isExpired(accessToken)) {
                throw new JwtException ("만료된 AccessToken 입니다.");
            }

            String email = jwtUtils.getEmail(accessToken);
            userEntity =
                userRepository.findByEmail(email)
                              .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        } catch (ExpiredJwtException e) {
            log.error("Access Token 만료");
            log.info("request.getRequestURI() : {}", request.getRequestURI());
            log.info("request.getRequestURL() : {}", request.getRequestURL());

            TokenEntity tokenEntity = tokenService.findByAccessToken(accessToken);

            String refreshToken = tokenEntity.getRefreshToken();

            if (jwtUtils.isExpired(refreshToken)) {
                // refresh Token 만료된 경우
                log.error("Refresh Token 만료");
                log.info("refreshToken : {}", refreshToken);
                throw new JwtException ("만료된 RefreshToken 입니다. 다시 로그인 해주세요.");
            }

            // refresh Token 유효한 경우 -> access Token / refresh Token 재발급
            Long userId = Long.valueOf(tokenEntity.getId());
            log.info("userId : {}", userId);
            String email = jwtUtils.getEmail(refreshToken);
            log.info("email : {}", email);
            String newAccessToken = jwtUtils.createAccessToken(email);
            log.info("newAccessToken : {}", newAccessToken);
            String newRefreshToken = jwtUtils.createRefreshToken(email);
            log.info("newRefreshToken : {}", newRefreshToken);

            userEntity =
                userRepository.findByEmail(email)
                              .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

            // 발급된 accessToken을 Redis 에 저장
            tokenService.saveToken(userId, newRefreshToken, newAccessToken);

            // 발급된 accessToken을 response header 에 저장
            HeaderUtils.addAccessTokenHeader(response, newAccessToken);
            // 발급된 refreshToken을 response cookie 에 저장
            CookieUtils.addRefreshTokenAtCookie(response, newRefreshToken);
        }

        // 유효성 검증 통과
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userEntity.getEmail(),
                                                    null,
                                                    List.of(new SimpleGrantedAuthority(userEntity.getRole().name())));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request,response);
    }
}
