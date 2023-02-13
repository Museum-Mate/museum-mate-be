package com.dev.museummate.global.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
public class HeaderUtils {

    private final static String BEARER = "Bearer ";
    private final static String ACCESS_TOKEN_HEADER = "Authorization";
    private final static String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    public static void addAccessTokenAtHeader(HttpServletResponse response, String token) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader(ACCESS_TOKEN_HEADER, BEARER + token);
    }

    public static Optional<String> extractAccessToken(HttpServletRequest request) {

        String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        return Optional.ofNullable(headerValue)
                       .filter(accessToken -> accessToken.startsWith(BEARER))
                       .map(accessToken -> accessToken.replace(BEARER, ""));
    }

}
