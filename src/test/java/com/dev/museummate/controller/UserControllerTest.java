package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.user.*;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("회원가입 - 성공")
    @WithMockUser
    void join_success() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .name("최민준")
                .password("1q2w3e4r!")
                .address("대한민국")
                .email("chlalsnws200@naver.com")
                .phoneNumber("01057442067")
                .birth("19981022")
                .build();
        UserDto userDto = UserDto.builder().userName(userJoinRequest.getUserName()).build();

        when(userService.join(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - 실패 #1 유저네임 중복")
    @WithMockUser
    void join_fail_1() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .name("최민준")
                .password("1q2w3e4r!")
                .address("대한민국")
                .email("chlalsnws200@naver.com")
                .phoneNumber("01057442067")
                .birth("19981022")
                .build();

        when(userService.join(any()))
                .thenThrow(new AppException(ErrorCode.DUPLICATE_USERNAME,"중복된 userName"));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 - 실패 #2 이메일 중복")
    @WithMockUser
    void join_fail_2() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .name("최민준")
                .password("1q2w3e4r!")
                .address("대한민국")
                .email("chlalsnws200@naver.com")
                .phoneNumber("01057442067")
                .birth("19981022")
                .build();

        when(userService.join(any()))
                .thenThrow(new AppException(ErrorCode.DUPLICATE_EMAIL,"중복된 이메일"));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인 - 성공")
    @WithMockUser
    void login_success() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest("chlalswns200@naver.com", "1q2w3e4r!");
        UserLoginResponse userLoginResponse = new UserLoginResponse("acceetoken1234","12315115");

        when(userService.login(any()))
                .thenReturn(userLoginResponse);

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("로그인 - 실패 #1 해당 유저 없음")
    @WithMockUser
    void login_fail_1() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest("chlalswns200@aver.com", "1q2w3e4r!");

        when(userService.login(any()))
                .thenThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND,""));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 - 실패 #2 비밀번호 틀림")
    @WithMockUser
    void login_fail_2() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest("chlalswns200@gmail.com", "1q2w3e4r!");

        when(userService.login(any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PASSWORD,""));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("재발급 - 성공")
    @WithMockUser
    void reissue_success() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.reissue(any(), any()))
                .willReturn(userLoginResponse);

        //when
        mockMvc.perform(post("/api/v1/users/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest))
                        )
                .andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("재발급 - 실패 #1 - 이메일 조회 실패")
    @WithMockUser
    void reissue_fail_1() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.reissue(any(), any()))
                .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, "이메일을 찾을 수 없습니다"));

        //when
        mockMvc.perform(post("/api/v1/users/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("재발급 - 실패 #2 - 인증 실패")
    @WithAnonymousUser
    void reissue_fail_2() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.reissue(any(), any()))
                .willThrow(new AppException(ErrorCode.EMAIL_NOT_FOUND, "이메일을 찾을 수 없습니다"));

        //when
        mockMvc.perform(post("/api/v1/users/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("재발급 - 실패 #3 - 잘못된 토큰")
    @WithMockUser
    void reissue_fail_3() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.reissue(any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다."));

        //when
        mockMvc.perform(post("/api/v1/users/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("재발급 - 실패 #4 - 잘못된 요청")
    @WithMockUser
    void reissue_fail_4() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.reissue(any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_REQUEST, "잘못된 요청입니다."));

        //when
        mockMvc.perform(post("/api/v1/users/reissue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    @WithMockUser
    void logout_success() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.logout(any(), any()))
                .willReturn("로그아웃 되었습니다.");

        //when
        mockMvc.perform(post("/api/v1/users/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그아웃 - 실패 #1 인증 실패")
    @WithAnonymousUser
    void logout_fail_1() throws Exception {

        UserReissueRequest userReissueRequest = new UserReissueRequest("actk", "rftk");
        UserLoginResponse userLoginResponse = new UserLoginResponse("actk-123", "rftk-123");

        //given
        given(userService.logout(any(), any()))
                .willReturn("로그아웃 되었습니다.");

        //when
        mockMvc.perform(post("/api/v1/users/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReissueRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



}