package com.dev.museummate.utils;

import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final UserService userService;

    private static final long accessExpireTimeMs = 1000 * 60 * 5;

    private static final long refreshExpireTimeMs = 1000 * 60 * 30;

    private static Claims extractClaims(String token, String secretKey){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String createAccessToken(String email, String secretKey, long expiredTimeMs){
        Claims claims = Jwts.claims();  //토큰의 내용에 값을 넣기 위해 Claims 객체 생성
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs))
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

    public static boolean isExpired(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());

    public static Long getExpiration(String accessToken, String secretKey) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    private UserEntity getUser(String token, String secretKey){
        String email = extractClaims(token, secretKey).get("email").toString();
        UserEntity userEntity = userService.findUserByEmail(email);
        return userEntity;
    }
    public UsernamePasswordAuthenticationToken getAuthentication(String token, String secretKey) {
        UserEntity userEntity = getUser(token,secretKey);
        return new UsernamePasswordAuthenticationToken(userEntity.getEmail(),
                null, List.of(new SimpleGrantedAuthority(userEntity.getRole().name())));
    }

    public static Long getExpiration(String accessToken, String secretKey) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
