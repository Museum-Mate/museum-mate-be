package com.dev.museummate.security;

import com.dev.museummate.configuration.redis.RedisDao;
import com.dev.museummate.controller.ExampleController;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.fixture.UserEntityFixture;
import com.dev.museummate.service.UserService;
import com.dev.museummate.utils.JwtUtils;
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
    UserService userService;

    @MockBean
    RedisDao redisDao;
    @MockBean
    JwtUtils jwtUtils;

    private UserEntity user;
    private UserEntity admin;

    private String createToken(UserEntity userEntity, long expiredMs){
        return jwtUtils.createAccessToken(userEntity.getEmail());
    }

    @BeforeEach
    void setUp(){
        user = UserEntityFixture.getUser("user_email","user_password");
        admin = UserEntityFixture.getAdmin("admin_email","admin_password");
    }

    @Test
    @DisplayName("인증 성공 테스트")
    void authenticationSuccess() throws Exception {
        String token = createToken(user,1000 * 60);

        when(redisDao.getValues(any())).thenReturn(null);

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(get("/api/v1/example/security")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("security test success"))
                .andDo(print());
    }


    @Test
    @DisplayName("인증 실패 테스트 - 만료된 토큰")
    void authenticationFailExpired() throws Exception {
        String token = createToken(user,1);

        mockMvc.perform(get("/api/v1/example/security")
                        .header("Authorization","Bearer " + token))
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
        String token = "abc";

        mockMvc.perform(get("/api/v1/example/security")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("인증 실패 테스트 - 로그아웃한 경우 ")
    void logOutUser() throws Exception {
        String token = createToken(user,1000 * 60);

        when(redisDao.getValues(any())).thenReturn("logout");

        mockMvc.perform(get("/api/v1/example/security")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("접근 성공 테스트 - admin 회원이 전용 페이지에 접근하는 경우")
    void accessSuccess() throws Exception {
        String token = createToken(admin,1000 * 60);

        when(redisDao.getValues(any())).thenReturn(null);

        when(userService.findUserByEmail(any())).thenReturn(admin);

        mockMvc.perform(get("/api/v1/example/security/admin")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("security test success"))
                .andDo(print());
    }

    @Test
    @DisplayName("접근 실패 테스트 - admin 회원 전용 페이지에 user 회원이 접근하는 경우")
    void accessDenied() throws Exception {
        String token = createToken(user,1000 * 60);

        when(redisDao.getValues(any())).thenReturn(null);

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(get("/api/v1/example/security/admin")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("FORBIDDEN_ACCESS"))
                .andExpect(jsonPath("$.result.message").exists())
                .andDo(print());
    }



    @Test
    @DisplayName("MethodNotAllowed 테스트 - 허용되지 않은 Http Method로 접근한 경우: 토큰 있을 때")
    void MethodNotAllowed() throws Exception {
        String token = createToken(user,1000 * 60);

        when(redisDao.getValues(any())).thenReturn(null);

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/example/security")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("MethodNotAllowed 테스트 - 허용되지 않은 Http Method로 접근한 경우: 토큰 없을 때")
    void MethodNotAllowed2() throws Exception {

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/example/security"))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("MethodNotAllowed 테스트 - 허용되지 않은 Http Method로 접근한 경우: 토큰 적절하지 않을 때")
    void MethodNotAllowed3() throws Exception {
        String token = "abc";

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(post("/api/v1/example/security")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("NotFound 테스트 - 정의되지 않은 url에 접근한 경우: 토큰 있을 때")
    void NotFound() throws Exception {
        String token = createToken(user,1000 * 60);

        when(redisDao.getValues(any())).thenReturn(null);

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(get("/api/v1/notDefinedUrl")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("NotFound 테스트 - 정의되지 않은 url에 접근한 경우: 토큰 없을 때")
    void NotFound2() throws Exception {
        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(get("/api/v1/notDefinedUrl"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("NotFound 테스트 - 정의되지 않은 url에 접근한 경우: 토큰 적절하지 않을 때")
    void NotFound3() throws Exception {
        String token = "abc";

        when(userService.findUserByEmail(any())).thenReturn(user);

        mockMvc.perform(get("/api/v1/notDefinedUrl")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
