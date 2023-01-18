package com.dev.musiummate.utils;

import com.dev.musiummate.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final UserService userService;

    private static Claims extractClaims(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public static String createToken(String userName, String secretKey, long expiredTimeMs){
        return "";
    }

}
