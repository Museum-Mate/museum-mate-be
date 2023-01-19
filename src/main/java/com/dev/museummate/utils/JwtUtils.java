package com.dev.museummate.utils;

import com.dev.museummate.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final UserService userService;

    private static final long accessExpireTimeMs = 1000 * 60 * 5;

    private static final long refreshExpireTimeMs = 1000 * 60 * 30;

    private static Claims extractClaims(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public static String createAccessToken(String email, String secretKey){
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpireTimeMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static String createRefreshToken(String email, String secretKey) {
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpireTimeMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
