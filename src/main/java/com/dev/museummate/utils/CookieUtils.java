package com.dev.museummate.utils;

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

    public static void addCookie(HttpServletResponse response, String key, String value, int maxAge) {

        ResponseCookie cookie = ResponseCookie.from(key, value)
                                              .httpOnly(true)
                                              .secure(true)
                                              .sameSite("Lax")
                                              .path("/")
                                              .maxAge(maxAge)
                                              .build();
        log.debug("method: createCooke cookie: {}", cookie.toString());

        // 헤더에 Set-Cookie 를 추가
        response.setHeader("Set-Cookie", cookie.toString());
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int maxAge) {

        ResponseCookie cookie = ResponseCookie.from(key, value)
                                              .httpOnly(true)
                                              .secure(true)
                                              .sameSite("Lax")
                                              .path("/")
                                              .maxAge(maxAge)
                                              .build();
        log.debug("method: createCooke cookie: {}", cookie.toString());

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
