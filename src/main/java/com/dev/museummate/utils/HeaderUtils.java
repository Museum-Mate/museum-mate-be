package com.dev.museummate.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class HeaderUtils {

    private final static String BEARER = "Bearer ";
    private final static String ACCESS_TOKEN_HEADER = "Authorization";
//    private final static String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    public static void setAccessTokenHeader(HttpServletResponse response, String token) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(ACCESS_TOKEN_HEADER, BEARER + token);
    }

    public static Optional<String> extractAccessToken(HttpServletRequest request) {

        String headerValue = request.getHeader(ACCESS_TOKEN_HEADER);

        return Optional.ofNullable(request.getHeader(ACCESS_TOKEN_HEADER))
                       .filter(accessToken -> accessToken.startsWith(BEARER))
                       .map(accessToken -> accessToken.replace(BEARER, ""));
    }

}
