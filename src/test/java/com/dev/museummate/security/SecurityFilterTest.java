package com.dev.museummate.security;

import com.dev.museummate.global.redis.RedisDao;
import com.dev.museummate.controller.ExampleController;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.fixture.UserEntityFixture;
import com.dev.museummate.global.security.SecurityConfiguration;
import com.dev.museummate.repository.UserRepository;
import com.dev.museummate.global.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ExampleController.class)
@ImportAutoConfiguration(classes = {SecurityConfiguration.class, JwtUtils.class})
public class SecurityFilterTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RedisDao redisDao;

    @Value("${jwt.secret}")
    public String secretKey;

    private UserEntity user;
    private UserEntity admin;

    private String createToken(String email, long expiredMs){
        Claims claims = Jwts.claims();
        claims.put("email", email);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    @BeforeEach
    void setUp(){
        user = UserEntityFixture.getUser("user_email","user_password");
        admin = UserEntityFixture.getAdmin("admin_email","admin_password");
    }

    @Test
    @DisplayName("인증 성공 테스트")
    void authenticationSuccess() throws Exception {
        String accessToken = createToken(user.getEmail(),1000 * 60);
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/example/security")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("security test success"))
                .andDo(print());
    }


    @Test
    @DisplayName("인증 실패 테스트 - 만료된 토큰")
    void authenticationFailExpired() throws Exception {
        String accessToken = createToken(user.getEmail(),1);
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        mockMvc.perform(get("/api/v1/example/security")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("EXPIRED_TOKEN"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("인증 실패 테스트 - 토큰 없음")
    void authenticationFailNull() throws Exception {

        mockMvc.perform(get("/api/v1/example/security"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("TOKEN_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("인증 실패 테스트 - 적절하지 않은 토큰(MalformedJwtException)")
    void authenticationFailWrong() throws Exception {
        String accessToken = "abc";
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        mockMvc.perform(get("/api/v1/example/security")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

//    @Test
//    @DisplayName("인증 실패 테스트 - 로그아웃한 경우 ")
//    void logOutUser() throws Exception {
//        String token = createToken(user.getEmail(),1000 * 60);
//
//        when(redisDao.getValues(any())).thenReturn("logout");
//
//        mockMvc.perform(get("/api/v1/example/security")
//                        .header("Authorization","Bearer " + token))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.resultCode").value("ERROR"))
//                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
//                .andExpect(jsonPath("$.result.message").exists())
//                .andDo(print());
//    }

    @Test
    @DisplayName("접근 성공 테스트 - admin 회원이 전용 페이지에 접근하는 경우")
    void accessSuccess() throws Exception {
        String accessToken = createToken(admin.getEmail(),1000 * 60);
        String refreshToken = createToken(admin.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(admin));

        mockMvc.perform(get("/api/v1/example/security/admin")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("security test success"))
                .andDo(print());
    }

    @Test
    @DisplayName("접근 실패 테스트 - admin 회원 전용 페이지에 user 회원이 접근하는 경우")
    void accessDenied() throws Exception {
        String accessToken = createToken(user.getEmail(),1000 * 60);
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/example/security/admin")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("FORBIDDEN_ACCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }



    @Test
    @DisplayName("MethodNotAllowed 테스트 - 허용되지 않은 Http Method로 접근한 경우: 토큰 있을 때")
    void MethodNotAllowed() throws Exception {
        String accessToken = createToken(user.getEmail(),1000 * 60);
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/example/security")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("MethodNotAllowed 테스트 - 허용되지 않은 Http Method로 접근한 경우: 토큰 없을 때")
    void MethodNotAllowed2() throws Exception {

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/example/security"))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("MethodNotAllowed 테스트 - 허용되지 않은 Http Method로 접근한 경우: 토큰 적절하지 않을 때")
    void MethodNotAllowed3() throws Exception {
        String accessToken = "abc";
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/example/security")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("NotFound 테스트 - 정의되지 않은 url에 접근한 경우: 토큰 있을 때")
    void NotFound() throws Exception {
        String accessToken = createToken(user.getEmail(),1000 * 60);
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/notDefinedUrl")
                            .cookie(accessCookie,refreshCookie))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("NotFound 테스트 - 정의되지 않은 url에 접근한 경우: 토큰 없을 때")
    void NotFound2() throws Exception {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/notDefinedUrl"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("NotFound 테스트 - 정의되지 않은 url에 접근한 경우: 토큰 적절하지 않을 때")
    void NotFound3() throws Exception {
        String accessToken = "abc";
        String refreshToken = createToken(user.getEmail(), 1000 * 60 * 30);

        Cookie accessCookie = new Cookie("Authorization", accessToken);
        Cookie refreshCookie = new Cookie("Authorization-refresh",refreshToken);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/notDefinedUrl")
                        .cookie(accessCookie,refreshCookie))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
