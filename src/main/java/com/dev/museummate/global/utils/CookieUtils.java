package com.dev.museummate.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

@Slf4j
public class CookieUtils {

    private final static int ACCESS_TOKEN_MAX_AGE = 60 * 60 * 3; // (seconds) -> 3시간
    private final static int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14; // (seconds) -> 14일
    //    private final static String BEARER = "Bearer ";
    private final static String ACCESS_TOKEN_HEADER = "Authorization";
    private final static String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    public static Optional<Cookie> getCookie(HttpServletRequest request, String key) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<String> extractAccessToken(HttpServletRequest request) {

        return getCookie(request, ACCESS_TOKEN_HEADER).map(Cookie::getValue);
    }

    public static Optional<String> extractRefreshToken(HttpServletRequest request) {

        return getCookie(request, REFRESH_TOKEN_HEADER).map(Cookie::getValue);
    }

    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge) {

        ResponseCookie cookie = ResponseCookie.from(key, value)
            .httpOnly(false)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(maxAge)
            .build();
        log.info("method: createCooke cookie: {}", cookie);

        // 헤더에 Set-Cookie 를 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addAccessTokenAtCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie =
            ResponseCookie.from(ACCESS_TOKEN_HEADER, value)
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
//                .domain("www.withmuma.com")
                .path("/")
                .maxAge(ACCESS_TOKEN_MAX_AGE)
                .build();

        log.info("method: createCooke cookie: {}", cookie);

        // 헤더에 Set-Cookie 를 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addRefreshTokenAtCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie =
            ResponseCookie.from(REFRESH_TOKEN_HEADER, value)
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
//                .domain("www.withmuma.com")
                .path("/")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .build();

        log.info("method: createCooke cookie: {}", cookie.toString());

        // 헤더에 Set-Cookie 를 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int maxAge) {

        ResponseCookie cookie = ResponseCookie.from(key, value)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/")
            .maxAge(maxAge)
            .build();
        log.info("method: createCooke cookie: {}", cookie.toString());

        // 헤더에 Set-Cookie 를 추가
        response.setHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String key) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {

                    ResponseCookie deleteCookie = ResponseCookie.from(key, "")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(0)
                        .build();

                    response.setHeader("Set-Cookie", deleteCookie.toString());
                }
            }
        }
    }

    // 직렬화
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    // 역직렬화
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
            SerializationUtils.deserialize(Base64.getUrlDecoder()
                                               .decode(cookie.getValue())));
    }

}
