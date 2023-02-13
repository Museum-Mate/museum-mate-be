package com.dev.museummate.global.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${jwt.secret}")
    public String secretKey;
    @Value("${jwt.access.expiration}")
    public Long accessTokenExpiration;
    @Value("${jwt.refresh.expiration}")
    public Long refreshTokenExpiration;

    public String createAccessToken(String email) {
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createAccessToken(String email, String name) {
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);
        claims.put("name", name);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }
    
    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String email, String name) {
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);
        claims.put("name", name);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    /**
     * 토큰이 만료되었는지 확인
     * @param token
     * @return
     */
    public boolean isExpired(String token) {
        return Jwts.parser()
                   .setSigningKey(secretKey)
                   .parseClaimsJws(token)
                   .getBody()
                   .getExpiration()
                   .before(new Date());
    }

    public boolean isValid(String token) {
        return !Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .isSigned(token);
    }

    public Long getExpiration(String token) {
        // token 남은 유효시간
        Date expiration = extractClaims(token).getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                   .setSigningKey(secretKey)
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String getEmail(String token) {
        return extractClaims(token).get("email")
                                   .toString();
    }

    public String getName(String token) {
        return extractClaims(token).get("name")
                                   .toString();
    }

}
